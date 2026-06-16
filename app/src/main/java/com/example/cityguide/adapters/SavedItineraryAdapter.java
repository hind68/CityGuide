package com.example.cityguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.SavedItinerary;
import com.example.cityguide.utils.ImageLoader;

import java.util.List;

public class SavedItineraryAdapter extends BaseAdapter {

    public interface OnItineraryActionListener {
        void onOpen(SavedItinerary itinerary);
        void onRemove(SavedItinerary itinerary);
    }

    private final List<SavedItinerary> itineraries;
    private final OnItineraryActionListener listener;

    public SavedItineraryAdapter(List<SavedItinerary> itineraries, OnItineraryActionListener listener) {
        this.itineraries = itineraries;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return itineraries.size();
    }

    @Override
    public SavedItinerary getItem(int position) {
        return itineraries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itineraries.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_itinerary, parent, false);
        }

        SavedItinerary itinerary = getItem(position);
        ImageLoader.load((ImageView) view.findViewById(R.id.imageItinerary), itinerary.getImage());
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(itinerary.getTitle());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(itinerary.getSubtitle());
        ((TextView) view.findViewById(R.id.textItemMeta)).setText(R.string.saved_route);
        view.findViewById(R.id.buttonOpenItinerary).setOnClickListener(v -> listener.onOpen(itinerary));
        Button removeButton = view.findViewById(R.id.buttonRemoveItinerary);
        removeButton.setOnClickListener(v -> listener.onRemove(itinerary));
        view.setOnClickListener(v -> listener.onOpen(itinerary));
        return view;
    }
}
