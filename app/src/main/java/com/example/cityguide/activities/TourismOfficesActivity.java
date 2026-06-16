package com.example.cityguide.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.adapters.TourismOfficeAdapter;
import com.example.cityguide.models.TourismOffice;
import com.example.cityguide.utils.IntentUtils;

import java.util.Arrays;
import java.util.List;

public class TourismOfficesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourism_offices);

        List<TourismOffice> offices = Arrays.asList(
                new TourismOffice("Moroccan National Tourism Office", "Rabat", "+212537278300", "+212537278300", "Rabat"),
                new TourismOffice("Marrakech Tourism Office", "Marrakech", "+212524433407", "+212524433407", "Medina, Marrakech"),
                new TourismOffice("Fes Tourism Office", "Fes", "+212535622460", "+212535622460", "Ville Nouvelle, Fes"),
                new TourismOffice("Casablanca Tourism Office", "Casablanca", "+212522271177", "+212522271177", "City center, Casablanca"),
                new TourismOffice("Chefchaouen Tourism Office", "Chefchaouen", "+212539986132", "+212539986132", "Medina, Chefchaouen")
        );

        ListView listOffices = findViewById(R.id.listTourismOffices);
        TextView header = new TextView(this);
        header.setText("Tourism Offices");
        header.setTextColor(getResources().getColor(R.color.deep_navy_blue));
        header.setTextSize(30);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, 0, 0, dp(16));
        header.setLayoutParams(new ListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        listOffices.addHeaderView(header, null, false);
        listOffices.setAdapter(new TourismOfficeAdapter(offices, new TourismOfficeAdapter.OnOfficeActionListener() {
            @Override
            public void onCall(TourismOffice office) {
                IntentUtils.callPhone(TourismOfficesActivity.this, office.getPhone());
            }

            @Override
            public void onSms(TourismOffice office) {
                IntentUtils.sendSms(TourismOfficesActivity.this, office.getSmsNumber(),
                        "Hello, I need tourism information about " + office.getCity() + ".");
            }
        }));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
