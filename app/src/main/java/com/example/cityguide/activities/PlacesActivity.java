package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.cityguide.R;
import com.example.cityguide.adapters.PlaceAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Place;
import com.example.cityguide.utils.Constants;

import java.util.ArrayList;
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
        View header = getLayoutInflater().inflate(R.layout.header_places, listPlaces, false);
        listPlaces.addHeaderView(header, null, false);
        spinnerCity = header.findViewById(R.id.spinnerCity);
        spinnerCategory = header.findViewById(R.id.spinnerCategory);

        setupSpinner(spinnerCity, databaseHelper.getPlaceCities());
        setupSpinner(spinnerCategory, databaseHelper.getPlaceCategories());
        selectSpinnerValue(spinnerCity, getIntent().getStringExtra(Constants.EXTRA_CITY));
        selectSpinnerValue(spinnerCategory, getIntent().getStringExtra(Constants.EXTRA_CATEGORY));
        header.findViewById(R.id.buttonApplyFilters).setOnClickListener(v -> loadPlaces());
        loadPlaces();
    }

    private void setupSpinner(Spinner spinner, List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(adapter);
    }

    private void selectSpinnerValue(Spinner spinner, String value) {
        if (value == null) {
            return;
        }
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
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
