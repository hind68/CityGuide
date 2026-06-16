package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Guide;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.ImageLoader;
import com.example.cityguide.utils.IntentUtils;
import com.example.cityguide.utils.SessionManager;

public class GuideDetailsActivity extends BaseActivity {

    private Guide guide;

    @Override
    protected int getPageTopInsetDp() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_details);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);
        guide = databaseHelper.getGuideById(getIntent().getIntExtra(Constants.EXTRA_GUIDE_ID, -1));
        if (guide == null) {
            finish();
            return;
        }

        ImageLoader.load((ImageView) findViewById(R.id.imageHero), guide.getImage(), R.drawable.user_placeholder);
        databaseHelper.addRecentView(getTrackingUserId(sessionManager), guide.getId(), Constants.FAVORITE_GUIDE);
        ((TextView) findViewById(R.id.textTitle)).setText(guide.getName());
        ((TextView) findViewById(R.id.textSubtitle)).setText(guide.getCity() + " - " + guide.getSpecialty());
        ((TextView) findViewById(R.id.textDescription)).setText(guide.getDescription());
        ((TextView) findViewById(R.id.textMeta)).setText(guide.getLanguages() + "\n"
                + getString(R.string.price_rating_label,
                String.valueOf(guide.getPricePerHour()),
                String.valueOf(guide.getRating())));

        findViewById(R.id.buttonCall).setOnClickListener(v -> IntentUtils.callPhone(this, guide.getPhone()));
        findViewById(R.id.buttonEmail).setOnClickListener(v -> IntentUtils.sendEmail(this, guide.getEmail(), "CityGuide+ booking request"));
        findViewById(R.id.buttonFavorite).setOnClickListener(v -> {
            if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
                Toast.makeText(this, R.string.sign_in_required_favorites, Toast.LENGTH_SHORT).show();
                return;
            }
            databaseHelper.addFavorite(sessionManager.getUserId(), guide.getId(), Constants.FAVORITE_GUIDE);
            Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.buttonBook).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservationActivity.class);
            intent.putExtra(Constants.EXTRA_GUIDE_ID, guide.getId());
            startActivity(intent);
        });
    }

    private int getTrackingUserId(SessionManager sessionManager) {
        return sessionManager.isLoggedIn() ? sessionManager.getUserId() : 0;
    }
}
