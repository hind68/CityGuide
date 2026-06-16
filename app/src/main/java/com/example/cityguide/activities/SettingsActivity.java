package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.User;
import com.example.cityguide.utils.SessionManager;

public class SettingsActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        profileName = findViewById(R.id.textProfileName);
        profileEmail = findViewById(R.id.textProfileEmail);
        profileStatus = findViewById(R.id.textProfileStatus);

        bindProfile();

        findViewById(R.id.buttonEditProfile).setOnClickListener(v -> showEditProfileDialog());
        findViewById(R.id.buttonMyReservations).setOnClickListener(v ->
                startActivity(new Intent(this, MyReservationsActivity.class)));
        findViewById(R.id.buttonFavorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.buttonRecentlyViewed).setOnClickListener(v ->
                startActivity(new Intent(this, RecentViewsActivity.class)));

        findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.buttonSignIn).setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null) {
            bindProfile();
        }
    }

    private void bindProfile() {
        boolean signedIn = sessionManager.isLoggedIn() && !sessionManager.isGuest() && sessionManager.getUserId() > 0;
        User user = signedIn ? databaseHelper.getUserById(sessionManager.getUserId()) : null;

        if (signedIn && user != null) {
            profileName.setText(user.getFullName());
            profileEmail.setText(user.getEmail());
            profileStatus.setText("SIGNED IN");
        } else if (sessionManager.isGuest()) {
            profileName.setText("Guest traveler");
            profileEmail.setText("Sign in to save favorites and reservations");
            profileStatus.setText("GUEST MODE");
        } else {
            profileName.setText("Traveler");
            profileEmail.setText("Sign in to personalize your journey");
            profileStatus.setText("NOT SIGNED IN");
        }

        findViewById(R.id.buttonEditProfile).setVisibility(signedIn ? View.VISIBLE : View.GONE);
        findViewById(R.id.buttonLogout).setVisibility(signedIn ? View.VISIBLE : View.GONE);
        findViewById(R.id.buttonSignIn).setVisibility(signedIn ? View.GONE : View.VISIBLE);
    }

    private void showEditProfileDialog() {
        if (!sessionManager.isLoggedIn() || sessionManager.isGuest()) {
            Toast.makeText(this, "Sign in to edit your profile.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = databaseHelper.getUserById(sessionManager.getUserId());
        if (user == null) {
            Toast.makeText(this, "Profile not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(32, 12, 32, 0);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Full name");
        nameInput.setSingleLine(true);
        nameInput.setText(user.getFullName());
        form.addView(nameInput);

        EditText phoneInput = new EditText(this);
        phoneInput.setHint("Phone");
        phoneInput.setSingleLine(true);
        phoneInput.setText(user.getPhone());
        form.addView(phoneInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String phone = phoneInput.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (databaseHelper.updateUserProfile(user.getId(), name, phone)) {
                        sessionManager.saveUserName(name);
                        bindProfile();
                        Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Could not update profile.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
