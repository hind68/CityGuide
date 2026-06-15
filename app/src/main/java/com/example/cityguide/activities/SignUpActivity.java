package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;

public class SignUpActivity extends BaseActivity {

    private EditText editFullName;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseHelper = new DatabaseHelper(this);
        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        findViewById(R.id.buttonCreateAccount).setOnClickListener(v -> createAccount());
        findViewById(R.id.textSignIn).setOnClickListener(v -> openSignIn());
    }

    private void createAccount() {
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (databaseHelper.isEmailExists(email)) {
            Toast.makeText(this, "This email is already registered.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean created = databaseHelper.createUser(fullName, email, password, phone);
        if (created) {
            Toast.makeText(this, "Account created. Please sign in.", Toast.LENGTH_SHORT).show();
            openSignIn();
        } else {
            Toast.makeText(this, "Could not create account.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSignIn() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}
