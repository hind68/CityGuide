package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.adapters.FavoriteAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Favorite;
import com.example.cityguide.utils.SessionManager;

import java.util.List;

public class FavoritesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        SessionManager sessionManager = new SessionManager(this);
        View emptyState = findViewById(R.id.emptyState);
        ListView listFavorites = findViewById(R.id.listFavorites);
        TextView emptyText = findViewById(R.id.textEmptyState);

        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            listFavorites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText("Sign in to keep your favorites across sessions.");
            findViewById(R.id.buttonSignIn).setOnClickListener(v ->
                    startActivity(new Intent(this, SignInActivity.class)));
            return;
        }

        List<Favorite> favorites = new DatabaseHelper(this).getFavorites(sessionManager.getUserId());
        if (favorites.isEmpty()) {
            listFavorites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText("Save places, guides and experiences to see them here.");
        } else {
            emptyState.setVisibility(View.GONE);
            listFavorites.setVisibility(View.VISIBLE);
            listFavorites.setAdapter(new FavoriteAdapter(favorites));
        }
    }
}
