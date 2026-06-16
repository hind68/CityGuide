package com.example.cityguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.TourismOffice;

import java.util.List;

public class TourismOfficeAdapter extends BaseAdapter {

    public interface OnOfficeActionListener {
        void onCall(TourismOffice office);
        void onSms(TourismOffice office);
    }

    private final List<TourismOffice> offices;
    private final OnOfficeActionListener listener;

    public TourismOfficeAdapter(List<TourismOffice> offices, OnOfficeActionListener listener) {
        this.offices = offices;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return offices.size();
    }

    @Override
    public TourismOffice getItem(int position) {
        return offices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tourism_office, parent, false);
        }

        TourismOffice office = getItem(position);
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(office.getName());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(office.getCity() + " - " + office.getAddress());
        ((TextView) view.findViewById(R.id.textItemDescription)).setText(office.getPhone());
        view.findViewById(R.id.buttonCallOffice).setOnClickListener(v -> listener.onCall(office));
        view.findViewById(R.id.buttonSmsOffice).setOnClickListener(v -> listener.onSms(office));
        return view;
    }
}
