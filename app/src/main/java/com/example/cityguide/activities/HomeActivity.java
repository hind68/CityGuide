package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.SessionManager;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SessionManager sessionManager = new SessionManager(this);
        TextView greeting = findViewById(R.id.textGreeting);
        if (sessionManager.isGuest()) {
            greeting.setText("Welcome, guest traveler");
        } else if (sessionManager.isLoggedIn()) {
            greeting.setText("Welcome, " + sessionManager.getUserName());
        } else {
            greeting.setText("Welcome to CityGuide+");
        }

        findViewById(R.id.actionSearch).setOnClickListener(v -> open(PlacesActivity.class));
        findViewById(R.id.categoryRestaurants).setOnClickListener(v -> open(ExperiencesActivity.class));
        findViewById(R.id.categoryCafes).setOnClickListener(v -> open(PlacesActivity.class));
        findViewById(R.id.categoryMonuments).setOnClickListener(v -> open(PlacesActivity.class));
        findViewById(R.id.categoryLeisure).setOnClickListener(v -> open(ExperiencesActivity.class));
        findViewById(R.id.collectionRooftops).setOnClickListener(v -> open(ExperiencesActivity.class));
        findViewById(R.id.collectionHiddenMedina).setOnClickListener(v -> open(PlacesActivity.class));
        findViewById(R.id.trendingPlace).setOnClickListener(v -> openPlaceDetails(4));
        findViewById(R.id.trendingCafe).setOnClickListener(v -> open(PlacesActivity.class));
        findViewById(R.id.guideAmina).setOnClickListener(v -> openGuideDetails(1));
        findViewById(R.id.guideOmar).setOnClickListener(v -> openGuideDetails(4));
        findViewById(R.id.recentPlace).setOnClickListener(v -> openPlaceDetails(7));
        findViewById(R.id.buttonExploreMap).setOnClickListener(v -> open(MapActivity.class));
    }

    private void open(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private void openPlaceDetails(int placeId) {
        Intent intent = new Intent(this, PlaceDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_PLACE_ID, placeId);
        startActivity(intent);
    }

    private void openGuideDetails(int guideId) {
        Intent intent = new Intent(this, GuideDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_GUIDE_ID, guideId);
        startActivity(intent);
    }
}
