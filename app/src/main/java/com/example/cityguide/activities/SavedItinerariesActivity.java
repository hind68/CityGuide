package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.adapters.SavedItineraryAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.SavedItinerary;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.SessionManager;

import java.util.List;

public class SavedItinerariesActivity extends BaseActivity {
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private View emptyState;
    private ListView listItineraries;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_itineraries);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        emptyState = findViewById(R.id.emptyState);
        listItineraries = findViewById(R.id.listItineraries);
        emptyText = findViewById(R.id.textEmptyState);
        loadItineraries();
    }

    private void loadItineraries() {
        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            listItineraries.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText(R.string.itineraries_sign_in_empty);
            return;
        }

        List<SavedItinerary> itineraries = databaseHelper.getSavedItineraries(sessionManager.getUserId());
        if (itineraries.isEmpty()) {
            listItineraries.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText(R.string.itineraries_empty);
            return;
        }

        emptyState.setVisibility(View.GONE);
        listItineraries.setVisibility(View.VISIBLE);
        listItineraries.setAdapter(new SavedItineraryAdapter(itineraries, new SavedItineraryAdapter.OnItineraryActionListener() {
            @Override
            public void onOpen(SavedItinerary itinerary) {
                Intent intent = new Intent(SavedItinerariesActivity.this, MapActivity.class);
                intent.putExtra(Constants.EXTRA_LATITUDE, itinerary.getLatitude());
                intent.putExtra(Constants.EXTRA_LONGITUDE, itinerary.getLongitude());
                intent.putExtra(Constants.EXTRA_LABEL, itinerary.getTitle());
                startActivity(intent);
            }

            @Override
            public void onRemove(SavedItinerary itinerary) {
                databaseHelper.removeSavedItinerary(itinerary.getId(), sessionManager.getUserId());
                Toast.makeText(SavedItinerariesActivity.this, R.string.itinerary_removed, Toast.LENGTH_SHORT).show();
                loadItineraries();
            }
        }));
    }
}
