package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.User;
import com.example.cityguide.utils.SessionManager;

public class SignInActivity extends BaseActivity {

    private EditText editEmail;
    private EditText editPassword;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        findViewById(R.id.buttonSignIn).setOnClickListener(v -> signIn());
        findViewById(R.id.buttonCreateAccount).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
        findViewById(R.id.buttonGuest).setOnClickListener(v -> continueAsGuest());
    }

    private void signIn() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = databaseHelper.loginUser(email, password);
        if (user == null) {
            Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.setLoggedIn(true);
        sessionManager.setGuest(false);
        sessionManager.saveUserId(user.getId());
        sessionManager.saveUserName(user.getFullName());
        openHome();
    }

    private void continueAsGuest() {
        sessionManager.setLoggedIn(false);
        sessionManager.setGuest(true);
        sessionManager.saveUserId(-1);
        sessionManager.saveUserName("Guest traveler");
        openHome();
    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
