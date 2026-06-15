package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.utils.SessionManager;

import java.util.Arrays;

public class SettingsActivity extends BaseActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);
        TextView profileSummary = findViewById(R.id.textProfileSummary);
        Spinner citySpinner = findViewById(R.id.spinnerDefaultCity);
        Spinner languageSpinner = findViewById(R.id.spinnerDefaultLanguage);
        Switch darkModeSwitch = findViewById(R.id.switchDarkMode);

        profileSummary.setText(sessionManager.isGuest()
                ? "Guest mode"
                : sessionManager.getUserName());
        findViewById(R.id.buttonLogout).setVisibility(sessionManager.isGuest() ? View.GONE : View.VISIBLE);
        findViewById(R.id.buttonSignIn).setVisibility(sessionManager.isGuest() ? View.VISIBLE : View.GONE);

        setupSpinner(citySpinner, new String[]{"Marrakech", "Fes", "Rabat", "Casablanca", "Chefchaouen"}, sessionManager.getDefaultCity());
        setupSpinner(languageSpinner, new String[]{"English", "French", "Arabic", "Spanish"}, sessionManager.getDefaultLanguage());
        darkModeSwitch.setChecked(sessionManager.isDarkMode());

        findViewById(R.id.buttonSaveSettings).setOnClickListener(v -> {
            sessionManager.saveDefaultCity(citySpinner.getSelectedItem().toString());
            sessionManager.saveDefaultLanguage(languageSpinner.getSelectedItem().toString());
            sessionManager.setDarkMode(darkModeSwitch.isChecked());
        });

        findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });

        findViewById(R.id.buttonSignIn).setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class)));
    }

    private void setupSpinner(Spinner spinner, String[] values, String selectedValue) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(values));
        spinner.setAdapter(adapter);
        int position = adapter.getPosition(selectedValue);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }
}
