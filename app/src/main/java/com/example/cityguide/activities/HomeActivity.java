package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Guide;
import com.example.cityguide.models.Place;
import com.example.cityguide.models.RecentView;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.ImageLoader;
import com.example.cityguide.utils.SessionManager;

public class HomeActivity extends BaseActivity {
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private RecentView latestRecentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        TextView greeting = findViewById(R.id.textGreeting);
        if (sessionManager.isGuest()) {
            greeting.setText("Welcome, guest traveler");
        } else if (sessionManager.isLoggedIn()) {
            greeting.setText("Welcome, " + sessionManager.getUserName());
        } else {
            greeting.setText("Welcome to CityGuide+");
        }

        findViewById(R.id.actionProfile).setOnClickListener(v -> open(SettingsActivity.class));
        findViewById(R.id.actionSearch).setOnClickListener(v -> open(PlacesActivity.class));
        findViewById(R.id.categoryRestaurants).setOnClickListener(v -> openPlacesCategory("Restaurant"));
        findViewById(R.id.categoryCafes).setOnClickListener(v -> openPlacesCategory("Cafe"));
        findViewById(R.id.categoryMonuments).setOnClickListener(v -> openPlacesCategory("Monument"));
        findViewById(R.id.categoryLeisure).setOnClickListener(v -> openPlacesCategory("Activity"));
        findViewById(R.id.collectionRooftops).setOnClickListener(v -> openPlacesCity("Marrakech"));
        findViewById(R.id.collectionHiddenMedina).setOnClickListener(v -> openPlacesCategory("Medina"));
        findViewById(R.id.collectionFood).setOnClickListener(v -> openExperiencesFilter(null, "Food"));
        findViewById(R.id.collectionCrafts).setOnClickListener(v -> openPlacesCategory("Activity"));
        findViewById(R.id.trendingPlace).setOnClickListener(v -> openPlaceDetails(databaseHelper, "Oudayas Kasbah"));
        findViewById(R.id.trendingCafe).setOnClickListener(v -> openPlaceDetails(databaseHelper, "Cafe Clock"));
        findViewById(R.id.trendingMajorelle).setOnClickListener(v -> openPlaceDetails(databaseHelper, "Jardin Majorelle"));
        findViewById(R.id.trendingAgafay).setOnClickListener(v -> openPlaceDetails(databaseHelper, "Agafay Desert Escape"));
        findViewById(R.id.favoriteTrendingPlace).setOnClickListener(v -> saveFavorite(databaseHelper, sessionManager, "Oudayas Kasbah"));
        findViewById(R.id.favoriteTrendingCafe).setOnClickListener(v -> saveFavorite(databaseHelper, sessionManager, "Cafe Clock"));
        findViewById(R.id.favoriteTrendingMajorelle).setOnClickListener(v -> saveFavorite(databaseHelper, sessionManager, "Jardin Majorelle"));
        findViewById(R.id.favoriteTrendingAgafay).setOnClickListener(v -> saveFavorite(databaseHelper, sessionManager, "Agafay Desert Escape"));
        findViewById(R.id.guideAmina).setOnClickListener(v -> openGuideDetails(databaseHelper, "Amina El Fassi"));
        findViewById(R.id.guideOmar).setOnClickListener(v -> openGuideDetails(databaseHelper, "Omar Chafik"));
        findViewById(R.id.guideNadia).setOnClickListener(v -> openGuideDetails(databaseHelper, "Nadia Amrani"));
        findViewById(R.id.guideYoussef).setOnClickListener(v -> openGuideDetails(databaseHelper, "Youssef Benali"));
        findViewById(R.id.recentPlace).setOnClickListener(v -> openLatestRecentView());
        findViewById(R.id.buttonExploreMap).setOnClickListener(v -> open(MapActivity.class));
        bindLatestRecentView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseHelper != null && sessionManager != null) {
            bindLatestRecentView();
        }
    }

    private void open(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private void openPlacesCategory(String category) {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra(Constants.EXTRA_CATEGORY, category);
        startActivity(intent);
    }

    private void openPlacesCity(String city) {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra(Constants.EXTRA_CITY, city);
        startActivity(intent);
    }

    private void openExperiencesFilter(String city, String category) {
        Intent intent = new Intent(this, ExperiencesActivity.class);
        if (city != null) {
            intent.putExtra(Constants.EXTRA_CITY, city);
        }
        if (category != null) {
            intent.putExtra(Constants.EXTRA_CATEGORY, category);
        }
        startActivity(intent);
    }

    private void saveFavorite(DatabaseHelper databaseHelper, SessionManager sessionManager, String placeName) {
        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Sign in to keep favorites across sessions.", Toast.LENGTH_SHORT).show();
            return;
        }
        int placeId = findPlaceId(databaseHelper, placeName);
        if (placeId == -1) {
            Toast.makeText(this, "Place not found yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHelper.addFavorite(sessionManager.getUserId(), placeId, Constants.FAVORITE_PLACE);
        Toast.makeText(this, "Added to favorites.", Toast.LENGTH_SHORT).show();
    }

    private void openPlaceDetails(DatabaseHelper databaseHelper, String placeName) {
        int placeId = findPlaceId(databaseHelper, placeName);
        if (placeId == -1) {
            open(PlacesActivity.class);
            return;
        }
        Intent intent = new Intent(this, PlaceDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_PLACE_ID, placeId);
        startActivity(intent);
    }

    private void openGuideDetails(DatabaseHelper databaseHelper, String guideName) {
        int guideId = findGuideId(databaseHelper, guideName);
        if (guideId == -1) {
            open(GuidesActivity.class);
            return;
        }
        Intent intent = new Intent(this, GuideDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_GUIDE_ID, guideId);
        startActivity(intent);
    }

    private void bindLatestRecentView() {
        java.util.List<RecentView> recentViews = databaseHelper.getRecentViews(getTrackingUserId(), 1);
        latestRecentView = recentViews.isEmpty() ? null : recentViews.get(0);

        ImageView imageRecent = findViewById(R.id.imageRecent);
        TextView title = findViewById(R.id.textRecentTitle);
        TextView subtitle = findViewById(R.id.textRecentSubtitle);

        if (latestRecentView == null) {
            ImageLoader.load(imageRecent, "place_chefchaouen");
            title.setText("Start Exploring");
            subtitle.setText("Recently viewed places will appear here");
            return;
        }

        ImageLoader.load(imageRecent, latestRecentView.getImage());
        title.setText(latestRecentView.getTitle());
        subtitle.setText(latestRecentView.getSubtitle());
    }

    private void openLatestRecentView() {
        if (latestRecentView == null) {
            open(PlacesActivity.class);
            return;
        }
        Intent intent;
        if (Constants.FAVORITE_PLACE.equals(latestRecentView.getItemType())) {
            intent = new Intent(this, PlaceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_PLACE_ID, latestRecentView.getItemId());
        } else if (Constants.FAVORITE_GUIDE.equals(latestRecentView.getItemType())) {
            intent = new Intent(this, GuideDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_GUIDE_ID, latestRecentView.getItemId());
        } else {
            intent = new Intent(this, ExperienceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_EXPERIENCE_ID, latestRecentView.getItemId());
        }
        startActivity(intent);
    }

    private int findPlaceId(DatabaseHelper databaseHelper, String placeName) {
        for (Place place : databaseHelper.getPlaces("All Cities", "All Categories")) {
            if (placeName.equals(place.getName())) {
                return place.getId();
            }
        }
        return -1;
    }

    private int findGuideId(DatabaseHelper databaseHelper, String guideName) {
        for (Guide guide : databaseHelper.getGuides()) {
            if (guideName.equals(guide.getName())) {
                return guide.getId();
            }
        }
        return -1;
    }

    private int getTrackingUserId() {
        return sessionManager.isLoggedIn() ? sessionManager.getUserId() : 0;
    }
}
