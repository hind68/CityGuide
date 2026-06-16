package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.adapters.ReservationAdapter;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Reservation;
import com.example.cityguide.utils.SessionManager;

import java.util.List;

public class MyReservationsActivity extends BaseActivity {
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private View emptyState;
    private ListView listReservations;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        emptyState = findViewById(R.id.emptyState);
        listReservations = findViewById(R.id.listReservations);
        emptyText = findViewById(R.id.textEmptyState);

        loadReservations();
    }

    private void loadReservations() {
        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            listReservations.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyText.setText(R.string.reservations_sign_in_empty);
            findViewById(R.id.buttonSignIn).setOnClickListener(v ->
                    startActivity(new Intent(this, SignInActivity.class)));
            return;
        }

        List<Reservation> reservations = databaseHelper.getReservations(sessionManager.getUserId());
        if (reservations.isEmpty()) {
            listReservations.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            findViewById(R.id.buttonSignIn).setVisibility(View.GONE);
            emptyText.setText(R.string.reservations_empty);
        } else {
            emptyState.setVisibility(View.GONE);
            listReservations.setVisibility(View.VISIBLE);
            listReservations.setAdapter(new ReservationAdapter(reservations, reservation -> {
                if (databaseHelper.cancelReservation(reservation.getId(), sessionManager.getUserId())) {
                    Toast.makeText(this, R.string.reservation_cancelled, Toast.LENGTH_SHORT).show();
                    loadReservations();
                } else {
                    Toast.makeText(this, R.string.reservation_cancel_error, Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }
}
