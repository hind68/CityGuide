package com.example.cityguide.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Place;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.IntentUtils;
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
    private static final LatLng MOROCCO_CENTER = new LatLng(31.7917, -7.0926);

    private DatabaseHelper databaseHelper;
    private FusedLocationProviderClient locationClient;
    private GoogleMap googleMap;
    private double latitude;
    private double longitude;
    private String label;
    private boolean hasTargetPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        databaseHelper = new DatabaseHelper(this);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        latitude = getIntent().getDoubleExtra(Constants.EXTRA_LATITUDE, MOROCCO_CENTER.latitude);
        longitude = getIntent().getDoubleExtra(Constants.EXTRA_LONGITUDE, MOROCCO_CENTER.longitude);
        label = getIntent().getStringExtra(Constants.EXTRA_LABEL);
        hasTargetPlace = label != null && !label.trim().isEmpty();
        if (!hasTargetPlace) {
            label = "Explore Morocco";
        }

        ((TextView) findViewById(R.id.textMapTitle)).setText(label);
        ((TextView) findViewById(R.id.textMapCoordinates)).setText(
                hasTargetPlace ? latitude + ", " + longitude : "Showing nearby places and Moroccan highlights"
        );
        findViewById(R.id.buttonOpenExternalMap).setOnClickListener(v ->
                IntentUtils.openMap(this, latitude, longitude, label));

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
            float markerColor = hasTargetPlace && place.getName().equals(label)
                    ? BitmapDescriptorFactory.HUE_AZURE
                    : BitmapDescriptorFactory.HUE_YELLOW;
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(place.getName())
                    .snippet(place.getCity() + " - " + place.getCategory())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
            if (marker != null) {
                marker.setTag(place);
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
        }
        return false;
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
                .title("Your location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        if (!hasTargetPlace) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13f));
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
}
