package com.example.cityguide.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.IntentUtils;

public class MapActivity extends BaseActivity {

    private double latitude;
    private double longitude;
    private String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        latitude = getIntent().getDoubleExtra(Constants.EXTRA_LATITUDE, 31.6258);
        longitude = getIntent().getDoubleExtra(Constants.EXTRA_LONGITUDE, -7.9891);
        label = getIntent().getStringExtra(Constants.EXTRA_LABEL);
        if (label == null) {
            label = "Morocco";
        }

        ((TextView) findViewById(R.id.textMapTitle)).setText(label);
        ((TextView) findViewById(R.id.textMapCoordinates)).setText(latitude + ", " + longitude);
        findViewById(R.id.buttonOpenExternalMap).setOnClickListener(v ->
                IntentUtils.openMap(this, latitude, longitude, label));
    }
}
