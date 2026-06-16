package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.adapters.RecentViewAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.RecentView;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.SessionManager;

import java.util.List;

public class RecentViewsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_views);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);
        int userId = sessionManager.isLoggedIn() ? sessionManager.getUserId() : 0;
        List<RecentView> recentViews = databaseHelper.getRecentViews(userId, 30);

        View emptyState = findViewById(R.id.emptyState);
        ListView listRecentViews = findViewById(R.id.listRecentViews);
        TextView emptyText = findViewById(R.id.textEmptyState);

        if (recentViews.isEmpty()) {
            listRecentViews.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText(R.string.recent_views_empty);
            return;
        }

        emptyState.setVisibility(View.GONE);
        listRecentViews.setVisibility(View.VISIBLE);
        listRecentViews.setAdapter(new RecentViewAdapter(recentViews, this::openRecentView));
    }

    private void openRecentView(RecentView recentView) {
        Intent intent;
        if (Constants.FAVORITE_PLACE.equals(recentView.getItemType())) {
            intent = new Intent(this, PlaceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_PLACE_ID, recentView.getItemId());
        } else if (Constants.FAVORITE_GUIDE.equals(recentView.getItemType())) {
            intent = new Intent(this, GuideDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_GUIDE_ID, recentView.getItemId());
        } else if (Constants.FAVORITE_EXPERIENCE.equals(recentView.getItemType())) {
            intent = new Intent(this, ExperienceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_EXPERIENCE_ID, recentView.getItemId());
        } else {
            Toast.makeText(this, R.string.favorite_details_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }
}
