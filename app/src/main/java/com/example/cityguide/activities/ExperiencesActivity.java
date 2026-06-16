package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.cityguide.R;
import com.example.cityguide.adapters.ExperienceAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Experience;
import com.example.cityguide.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ExperiencesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiences);

        ListView listExperiences = findViewById(R.id.listExperiences);
        listExperiences.addHeaderView(getLayoutInflater().inflate(R.layout.header_experiences, listExperiences, false), null, false);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        listExperiences.setAdapter(new ExperienceAdapter(filterExperiences(databaseHelper.getExperiences()), experience -> {
            Intent intent = new Intent(this, ExperienceDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_EXPERIENCE_ID, experience.getId());
            startActivity(intent);
        }));
    }

    private List<Experience> filterExperiences(List<Experience> experiences) {
        String cityFilter = getIntent().getStringExtra(Constants.EXTRA_CITY);
        String categoryFilter = getIntent().getStringExtra(Constants.EXTRA_CATEGORY);
        if ((cityFilter == null || cityFilter.trim().isEmpty())
                && (categoryFilter == null || categoryFilter.trim().isEmpty())) {
            return experiences;
        }

        List<Experience> filtered = new ArrayList<>();
        for (Experience experience : experiences) {
            if (cityFilter != null && !cityFilter.trim().isEmpty()
                    && !cityFilter.equals(experience.getCity())) {
                continue;
            }
            if (categoryFilter != null && !categoryFilter.trim().isEmpty()
                    && !matchesCategory(experience.getCategory(), categoryFilter)) {
                continue;
            }
            filtered.add(experience);
        }
        return filtered;
    }

    private boolean matchesCategory(String category, String filter) {
        String[] allowedCategories = filter.split(",");
        for (String allowedCategory : allowedCategories) {
            if (allowedCategory.trim().equals(category)) {
                return true;
            }
        }
        return false;
    }
}
