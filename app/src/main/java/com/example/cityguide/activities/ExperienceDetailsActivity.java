package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Experience;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.SessionManager;

public class ExperienceDetailsActivity extends BaseActivity {

    private Experience experience;

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

        ((TextView) findViewById(R.id.textTitle)).setText(experience.getTitle());
        ((TextView) findViewById(R.id.textSubtitle)).setText(experience.getCity() + " · " + experience.getCategory());
        ((TextView) findViewById(R.id.textDescription)).setText(experience.getDescription());
        ((TextView) findViewById(R.id.textMeta)).setText(experience.getDuration() + " · " + experience.getPrice() + " MAD · Rating " + experience.getRating());

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
}
