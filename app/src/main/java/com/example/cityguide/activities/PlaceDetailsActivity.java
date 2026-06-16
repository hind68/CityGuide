package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Place;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.ImageLoader;
import com.example.cityguide.utils.IntentUtils;
import com.example.cityguide.utils.SessionManager;

public class PlaceDetailsActivity extends BaseActivity {

    private Place place;

    @Override
    protected int getPageTopInsetDp() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);
        int placeId = getIntent().getIntExtra(Constants.EXTRA_PLACE_ID, -1);
        place = databaseHelper.getPlaceById(placeId);
        if (place == null) {
            finish();
            return;
        }

        ImageLoader.load((ImageView) findViewById(R.id.imageHero), place.getImage());
        databaseHelper.addRecentView(getTrackingUserId(sessionManager), place.getId(), Constants.FAVORITE_PLACE);
        ((TextView) findViewById(R.id.textTitle)).setText(place.getName());
        ((TextView) findViewById(R.id.textSubtitle)).setText(place.getCity() + " - " + place.getCategory());
        ((TextView) findViewById(R.id.textDescription)).setText(place.getDescription());
        ((TextView) findViewById(R.id.textAddress)).setText(place.getAddress());
        ((TextView) findViewById(R.id.textMeta)).setText("Rating " + place.getRating() + " - " + place.getPhone());

        findViewById(R.id.buttonOpenMap).setOnClickListener(v ->
                IntentUtils.openMap(this, place.getLatitude(), place.getLongitude(), place.getName()));
        findViewById(R.id.buttonCall).setOnClickListener(v -> IntentUtils.callPhone(this, place.getPhone()));
        findViewById(R.id.buttonFavorite).setOnClickListener(v -> {
            if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Sign in to keep favorites across sessions.", Toast.LENGTH_SHORT).show();
                return;
            }
            databaseHelper.addFavorite(sessionManager.getUserId(), place.getId(), Constants.FAVORITE_PLACE);
            Toast.makeText(this, "Added to favorites.", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.buttonViewMapScreen).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(Constants.EXTRA_LATITUDE, place.getLatitude());
            intent.putExtra(Constants.EXTRA_LONGITUDE, place.getLongitude());
            intent.putExtra(Constants.EXTRA_LABEL, place.getName());
            startActivity(intent);
        });
    }

    private int getTrackingUserId(SessionManager sessionManager) {
        return sessionManager.isLoggedIn() ? sessionManager.getUserId() : 0;
    }
}
