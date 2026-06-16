package com.example.cityguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Experience;
import com.example.cityguide.models.Guide;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.SessionManager;

import java.util.Arrays;

public class ReservationActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner spinnerHours;
    private EditText editMessage;
    private TextView textTitle;
    private TextView textTotal;
    private int guideId = -1;
    private int experienceId = -1;
    private double basePrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        guideId = getIntent().getIntExtra(Constants.EXTRA_GUIDE_ID, -1);
        experienceId = getIntent().getIntExtra(Constants.EXTRA_EXPERIENCE_ID, -1);

        textTitle = findViewById(R.id.textReservationTitle);
        textTotal = findViewById(R.id.textTotalPrice);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        spinnerHours = findViewById(R.id.spinnerHours);
        editMessage = findViewById(R.id.editMessage);
        timePicker.setIs24HourView(true);

        spinnerHours.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("1", "2", "3", "4", "5", "6")));
        spinnerHours.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateTotal();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        setupReservationSubject();
        findViewById(R.id.buttonConfirmReservation).setOnClickListener(v -> confirmReservation());
    }

    private void setupReservationSubject() {
        if (guideId > 0) {
            Guide guide = databaseHelper.getGuideById(guideId);
            if (guide != null) {
                textTitle.setText(getString(R.string.book_item, guide.getName()));
                basePrice = guide.getPricePerHour();
            }
        } else if (experienceId > 0) {
            Experience experience = databaseHelper.getExperienceById(experienceId);
            if (experience != null) {
                textTitle.setText(getString(R.string.book_item, experience.getTitle()));
                basePrice = experience.getPrice();
            }
        } else {
            textTitle.setText(R.string.new_reservation);
            basePrice = 200;
        }
        updateTotal();
    }

    private void updateTotal() {
        if (spinnerHours.getSelectedItem() == null) {
            return;
        }
        int hours = Integer.parseInt(spinnerHours.getSelectedItem().toString());
        textTotal.setText(getString(R.string.total_price, String.valueOf(basePrice * hours)));
    }

    private void confirmReservation() {
        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, R.string.signin_to_save_reservations, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInActivity.class));
            return;
        }
        int hours = Integer.parseInt(spinnerHours.getSelectedItem().toString());
        String date = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
        String time = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());
        long id = databaseHelper.addReservation(
                sessionManager.getUserId(),
                guideId,
                experienceId,
                date,
                time,
                hours,
                basePrice * hours,
                editMessage.getText().toString().trim()
        );
        if (id != -1) {
            startActivity(new Intent(this, MyReservationsActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.reservation_save_error, Toast.LENGTH_SHORT).show();
        }
    }
}
