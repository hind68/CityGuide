package com.example.cityguide.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Place;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.IntentUtils;
import com.example.cityguide.utils.NotificationHelper;
import com.example.cityguide.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 42;
    private static final int NOTIFICATION_REQUEST_CODE = 43;
    private static final LatLng MOROCCO_CENTER = new LatLng(31.7917, -7.0926);

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private FusedLocationProviderClient locationClient;
    private GoogleMap googleMap;
    private Place selectedPlace;
    private double latitude;
    private double longitude;
    private String label;
    private boolean hasTargetPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        NotificationHelper.createChannels(this);
        requestNotificationPermissionIfNeeded();

        latitude = getIntent().getDoubleExtra(Constants.EXTRA_LATITUDE, MOROCCO_CENTER.latitude);
        longitude = getIntent().getDoubleExtra(Constants.EXTRA_LONGITUDE, MOROCCO_CENTER.longitude);
        label = getIntent().getStringExtra(Constants.EXTRA_LABEL);
        hasTargetPlace = label != null && !label.trim().isEmpty();
        if (!hasTargetPlace) {
            label = getString(R.string.explore_morocco);
        }

        ((TextView) findViewById(R.id.textMapTitle)).setText(label);
        ((TextView) findViewById(R.id.textMapCoordinates)).setText(
                hasTargetPlace ? latitude + ", " + longitude : getString(R.string.map_default_subtitle)
        );
        findViewById(R.id.buttonOpenExternalMap).setOnClickListener(v ->
                IntentUtils.openMap(this, latitude, longitude, label));
        findViewById(R.id.buttonSaveVisitedPlace).setOnClickListener(v -> saveSelectedPlace());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMarkerClickListener(this::handleMarkerClick);

        addPlaceMarkers();

        if (hasTargetPlace) {
            LatLng target = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 14f));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MOROCCO_CENTER, 5.6f));
        }

        enableUserLocation();
    }

    private void addPlaceMarkers() {
        List<Place> places = databaseHelper.getPlaces("All Cities", "All Categories");
        for (Place place : places) {
            LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
            boolean isSaved = canSaveItinerary() && databaseHelper.isSavedItinerary(sessionManager.getUserId(), place.getId());
            boolean isTarget = hasTargetPlace && place.getName().equals(label);
            float markerColor = isSaved
                    ? BitmapDescriptorFactory.HUE_GREEN
                    : isTarget ? BitmapDescriptorFactory.HUE_AZURE : BitmapDescriptorFactory.HUE_YELLOW;
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(place.getName())
                    .snippet(place.getCity() + " - " + place.getCategory())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
            if (marker != null) {
                marker.setTag(place);
            }
            if (isTarget) {
                selectedPlace = place;
                updateSaveButton();
            }
        }
    }

    private boolean handleMarkerClick(Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof Place) {
            Place place = (Place) tag;
            latitude = place.getLatitude();
            longitude = place.getLongitude();
            label = place.getName();
            ((TextView) findViewById(R.id.textMapTitle)).setText(place.getName());
            ((TextView) findViewById(R.id.textMapCoordinates)).setText(place.getCity() + " - " + place.getCategory());
            selectedPlace = place;
            updateSaveButton();
        }
        return false;
    }

    private void saveSelectedPlace() {
        if (!canSaveItinerary()) {
            Toast.makeText(this, R.string.signin_to_save_visited, Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPlace == null) {
            Toast.makeText(this, R.string.choose_marker_first, Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHelper.addSavedItinerary(sessionManager.getUserId(), selectedPlace.getId());
        Toast.makeText(this, R.string.visit_saved, Toast.LENGTH_SHORT).show();
        refreshMarkers();
        updateSaveButton();
    }

    private boolean canSaveItinerary() {
        return sessionManager != null && sessionManager.isLoggedIn() && !sessionManager.isGuest();
    }

    private void updateSaveButton() {
        View button = findViewById(R.id.buttonSaveVisitedPlace);
        if (button == null) {
            return;
        }
        button.setVisibility(selectedPlace == null ? View.GONE : View.VISIBLE);
        if (selectedPlace != null && canSaveItinerary()
                && databaseHelper.isSavedItinerary(sessionManager.getUserId(), selectedPlace.getId())) {
            ((TextView) button).setText(R.string.saved_as_visited);
        } else {
            ((TextView) button).setText(R.string.save_this_visit);
        }
    }

    private void refreshMarkers() {
        if (googleMap == null) {
            return;
        }
        googleMap.clear();
        addPlaceMarkers();
    }

    private void enableUserLocation() {
        if (googleMap == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        locationClient.getLastLocation().addOnSuccessListener(this, this::showCurrentLocation);
    }

    private void showCurrentLocation(Location location) {
        if (location == null || googleMap == null) {
            return;
        }
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .title(getString(R.string.your_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        if (!hasTargetPlace) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13f));
        }
        autoSaveVisitedPlace(location);
        new NearbySuggestionTask(this, databaseHelper.getPlaces("All Cities", "All Categories")).execute(location);
    }

    private void autoSaveVisitedPlace(Location location) {
        if (!canSaveItinerary() || location == null) {
            return;
        }
        Place nearestVisited = null;
        float nearestDistance = Float.MAX_VALUE;
        for (Place place : databaseHelper.getPlaces("All Cities", "All Categories")) {
            float[] result = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    place.getLatitude(), place.getLongitude(), result);
            if (result[0] < nearestDistance) {
                nearestDistance = result[0];
                nearestVisited = place;
            }
        }
        if (nearestVisited != null && nearestDistance <= 120f) {
            databaseHelper.addSavedItinerary(sessionManager.getUserId(), nearestVisited.getId());
            selectedPlace = nearestVisited;
            refreshMarkers();
            updateSaveButton();
            Toast.makeText(this, getString(R.string.visited_auto_saved, nearestVisited.getName()), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        }
    }

    private static class NearbySuggestionTask extends AsyncTask<Location, Void, Place> {
        private final MapActivity activity;
        private final List<Place> places;

        NearbySuggestionTask(MapActivity activity, List<Place> places) {
            this.activity = activity;
            this.places = places;
        }

        @Override
        protected Place doInBackground(Location... locations) {
            if (locations.length == 0 || locations[0] == null || places == null || places.isEmpty()) {
                return null;
            }
            Location current = locations[0];
            Place nearest = null;
            float nearestDistance = Float.MAX_VALUE;
            for (Place place : places) {
                float[] result = new float[1];
                Location.distanceBetween(current.getLatitude(), current.getLongitude(),
                        place.getLatitude(), place.getLongitude(), result);
                if (result[0] < nearestDistance) {
                    nearestDistance = result[0];
                    nearest = place;
                }
            }
            return nearest;
        }

        @Override
        protected void onPostExecute(Place place) {
            if (place == null || activity.isFinishing()) {
                return;
            }
            ((TextView) activity.findViewById(R.id.textMapCoordinates))
                    .setText(activity.getString(R.string.nearest_suggestion, place.getName(), place.getCity()));
            NotificationHelper.notifyNearbyPlace(activity, place);
        }
    }
}
