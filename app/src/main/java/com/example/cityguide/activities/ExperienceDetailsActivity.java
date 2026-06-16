package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Experience;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.ImageLoader;
import com.example.cityguide.utils.SessionManager;

public class ExperienceDetailsActivity extends BaseActivity {

    private Experience experience;

    @Override
    protected int getPageTopInsetDp() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_details);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);
        experience = databaseHelper.getExperienceById(getIntent().getIntExtra(Constants.EXTRA_EXPERIENCE_ID, -1));
        if (experience == null) {
            finish();
            return;
        }

        ImageLoader.load((ImageView) findViewById(R.id.imageHero), experience.getImage());
        databaseHelper.addRecentView(getTrackingUserId(sessionManager), experience.getId(), Constants.FAVORITE_EXPERIENCE);
        ((TextView) findViewById(R.id.textTitle)).setText(experience.getTitle());
        ((TextView) findViewById(R.id.textSubtitle)).setText(experience.getCity() + " - " + experience.getCategory());
        ((TextView) findViewById(R.id.textDescription)).setText(experience.getDescription());
        ((TextView) findViewById(R.id.textMeta)).setText(experience.getDuration() + " - " + experience.getPrice() + " MAD - Rating " + experience.getRating());

        findViewById(R.id.buttonFavorite).setOnClickListener(v -> {
            if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Sign in to keep favorites across sessions.", Toast.LENGTH_SHORT).show();
                return;
            }
            databaseHelper.addFavorite(sessionManager.getUserId(), experience.getId(), Constants.FAVORITE_EXPERIENCE);
            Toast.makeText(this, "Added to favorites.", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.buttonBook).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservationActivity.class);
            intent.putExtra(Constants.EXTRA_EXPERIENCE_ID, experience.getId());
            startActivity(intent);
        });
    }

    private int getTrackingUserId(SessionManager sessionManager) {
        return sessionManager.isLoggedIn() ? sessionManager.getUserId() : 0;
    }
}
