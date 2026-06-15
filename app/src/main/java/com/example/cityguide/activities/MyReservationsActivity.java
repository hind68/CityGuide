package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.adapters.ReservationAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Reservation;
import com.example.cityguide.utils.SessionManager;

import java.util.List;

public class MyReservationsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        SessionManager sessionManager = new SessionManager(this);
        View emptyState = findViewById(R.id.emptyState);
        ListView listReservations = findViewById(R.id.listReservations);
        TextView emptyText = findViewById(R.id.textEmptyState);

        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            listReservations.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText("Sign in to save and manage your reservations.");
            findViewById(R.id.buttonSignIn).setOnClickListener(v ->
                    startActivity(new Intent(this, SignInActivity.class)));
            return;
        }

        List<Reservation> reservations = new DatabaseHelper(this).getReservations(sessionManager.getUserId());
        if (reservations.isEmpty()) {
            listReservations.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText("Your reservations will appear here after booking a guide or experience.");
        } else {
            emptyState.setVisibility(View.GONE);
            listReservations.setVisibility(View.VISIBLE);
            listReservations.setAdapter(new ReservationAdapter(reservations));
        }
    }
}
