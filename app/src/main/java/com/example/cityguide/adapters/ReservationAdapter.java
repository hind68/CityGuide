package com.example.cityguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.Reservation;

import java.util.List;

public class ReservationAdapter extends BaseAdapter {

    public interface OnReservationActionListener {
        void onCancel(Reservation reservation);
    }

    private final List<Reservation> reservations;
    private final OnReservationActionListener listener;

    public ReservationAdapter(List<Reservation> reservations, OnReservationActionListener listener) {
        this.reservations = reservations;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return reservations.size();
    }

    @Override
    public Reservation getItem(int position) {
        return reservations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return reservations.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        }
        Reservation reservation = getItem(position);
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(reservation.getTitle());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(parent.getContext().getString(
                R.string.date_at_time, reservation.getDate(), reservation.getTime()));
        ((TextView) view.findViewById(R.id.textItemDescription)).setText(parent.getContext().getString(
                R.string.hours_price_label,
                String.valueOf(reservation.getNumberOfHours()),
                String.valueOf(reservation.getTotalPrice())));
        ((TextView) view.findViewById(R.id.textStatusBadge)).setText(reservation.getStatus());
        Button cancelButton = view.findViewById(R.id.buttonCancelReservation);
        boolean isCancelled = "Cancelled".equalsIgnoreCase(reservation.getStatus());
        cancelButton.setVisibility(isCancelled ? View.GONE : View.VISIBLE);
        cancelButton.setOnClickListener(v -> listener.onCancel(reservation));
        return view;
    }
}
