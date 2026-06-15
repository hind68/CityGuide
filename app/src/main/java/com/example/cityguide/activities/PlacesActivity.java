package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.cityguide.R;
import com.example.cityguide.adapters.PlaceAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Place;
import com.example.cityguide.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacesActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;
    private ListView listPlaces;
    private Spinner spinnerCity;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        databaseHelper = new DatabaseHelper(this);
        listPlaces = findViewById(R.id.listPlaces);
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        setupSpinner(spinnerCity, Arrays.asList("All Cities", "Marrakech", "Fes", "Rabat", "Casablanca", "Chefchaouen"));
        setupSpinner(spinnerCategory, Arrays.asList("All Categories", "Garden", "Monument", "Medina", "Souk", "Landmark"));
        findViewById(R.id.buttonApplyFilters).setOnClickListener(v -> loadPlaces());
        loadPlaces();
    }

    private void setupSpinner(Spinner spinner, List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(adapter);
    }

    private void loadPlaces() {
        List<Place> places = new ArrayList<>(databaseHelper.getPlaces(
                spinnerCity.getSelectedItem().toString(),
                spinnerCategory.getSelectedItem().toString()));
        listPlaces.setAdapter(new PlaceAdapter(places, new PlaceAdapter.OnPlaceActionListener() {
            @Override
            public void onDetails(Place place) {
                Intent intent = new Intent(PlacesActivity.this, PlaceDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_PLACE_ID, place.getId());
                startActivity(intent);
            }

            @Override
            public void onMap(Place place) {
                Intent intent = new Intent(PlacesActivity.this, MapActivity.class);
                intent.putExtra(Constants.EXTRA_LATITUDE, place.getLatitude());
                intent.putExtra(Constants.EXTRA_LONGITUDE, place.getLongitude());
                intent.putExtra(Constants.EXTRA_LABEL, place.getName());
                startActivity(intent);
            }
        }));
    }
}
