package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.adapters.FavoriteAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Favorite;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.SessionManager;

import java.util.List;

public class FavoritesActivity extends BaseActivity {
    private DatabaseHelper databaseHelper;
    private ListView listFavorites;
    private View emptyState;
    private TextView emptyText;
    private View signInButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        emptyState = findViewById(R.id.emptyState);
        listFavorites = findViewById(R.id.listFavorites);
        emptyText = findViewById(R.id.textEmptyState);
        signInButton = findViewById(R.id.buttonSignIn);
        signInButton.setOnClickListener(v -> startActivity(new Intent(this, SignInActivity.class)));

        refreshFavoritesState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null) {
            refreshFavoritesState();
        }
    }

    private void refreshFavoritesState() {
        if (!canShowFavorites()) {
            listFavorites.setAdapter(null);
            listFavorites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            emptyText.setText("Sign in to keep your favorites across sessions.");
            return;
        }

        loadFavorites();
    }

    private boolean canShowFavorites() {
        return sessionManager.isLoggedIn() && !sessionManager.isGuest() && sessionManager.getUserId() > 0;
    }

    private void loadFavorites() {
        List<Favorite> favorites = databaseHelper.getFavorites(sessionManager.getUserId());
        if (favorites.isEmpty()) {
            listFavorites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            emptyText.setText("Save places, guides and experiences to see them here.");
        } else {
            emptyState.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
            listFavorites.setVisibility(View.VISIBLE);
            listFavorites.setAdapter(new FavoriteAdapter(favorites, new FavoriteAdapter.OnFavoriteActionListener() {
                @Override
                public void onRemove(Favorite favorite) {
                    databaseHelper.removeFavorite(favorite.getId());
                    Toast.makeText(FavoritesActivity.this, "Removed from favorites.", Toast.LENGTH_SHORT).show();
                    loadFavorites();
                }

                @Override
                public void onOpen(Favorite favorite) {
                    openFavoriteDetails(favorite);
                }
            }));
        }
    }

    private void openFavoriteDetails(Favorite favorite) {
        Intent intent;
        if (Constants.FAVORITE_PLACE.equals(favorite.getItemType())) {
            intent = new Intent(this, PlaceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_PLACE_ID, favorite.getItemId());
        } else if (Constants.FAVORITE_GUIDE.equals(favorite.getItemType())) {
            intent = new Intent(this, GuideDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_GUIDE_ID, favorite.getItemId());
        } else if (Constants.FAVORITE_EXPERIENCE.equals(favorite.getItemType())) {
            intent = new Intent(this, ExperienceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_EXPERIENCE_ID, favorite.getItemId());
        } else {
            Toast.makeText(this, "Details are not available yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }
}
