package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.cityguide.R;
import com.example.cityguide.adapters.ExperienceAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.utils.Constants;

public class ExperiencesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiences);

        ListView listExperiences = findViewById(R.id.listExperiences);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        listExperiences.setAdapter(new ExperienceAdapter(databaseHelper.getExperiences(), experience -> {
            Intent intent = new Intent(this, ExperienceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_EXPERIENCE_ID, experience.getId());
            startActivity(intent);
        }));
    }
}
