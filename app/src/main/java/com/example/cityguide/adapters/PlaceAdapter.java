package com.example.cityguide.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.Place;
import com.example.cityguide.utils.ImageLoader;

import java.util.List;

public class PlaceAdapter extends BaseAdapter {

    public interface OnPlaceActionListener {
        void onDetails(Place place);
        void onMap(Place place);
    }

    private final List<Place> places;
    private final OnPlaceActionListener listener;

    public PlaceAdapter(List<Place> places, OnPlaceActionListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Place getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return places.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        }

        Place place = getItem(position);
        ImageLoader.load((ImageView) view.findViewById(R.id.imageItem), place.getImage());
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(place.getName());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(place.getCity() + " - " + place.getCategory());
        ((TextView) view.findViewById(R.id.textItemDescription)).setText(place.getDescription());
        ((TextView) view.findViewById(R.id.textItemMeta)).setText(parent.getContext().getString(
                R.string.rating_label, String.valueOf(place.getRating())));

        Button detailsButton = view.findViewById(R.id.buttonPrimary);
        Button mapButton = view.findViewById(R.id.buttonSecondary);
        detailsButton.setBackgroundTintList((ColorStateList) null);
        mapButton.setBackgroundTintList((ColorStateList) null);
        detailsButton.setText(R.string.view_details);
        mapButton.setText(R.string.view_map);
        detailsButton.setOnClickListener(v -> listener.onDetails(place));
        mapButton.setOnClickListener(v -> listener.onMap(place));
        view.setOnClickListener(v -> listener.onDetails(place));
        return view;
    }
}
