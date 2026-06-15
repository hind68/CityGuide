package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.cityguide.R;
import com.example.cityguide.adapters.GuideAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Guide;
import com.example.cityguide.utils.Constants;

public class GuidesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);

        ListView listGuides = findViewById(R.id.listGuides);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        listGuides.setAdapter(new GuideAdapter(databaseHelper.getGuides(), guide -> {
            Intent intent = new Intent(this, GuideDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_GUIDE_ID, guide.getId());
            startActivity(intent);
        }));
    }
}
