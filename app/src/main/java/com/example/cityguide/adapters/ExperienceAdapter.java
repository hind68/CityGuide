package com.example.cityguide.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.Experience;

import java.util.List;

public class ExperienceAdapter extends BaseAdapter {

    public interface OnExperienceActionListener {
        void onDetails(Experience experience);
    }

    private final List<Experience> experiences;
    private final OnExperienceActionListener listener;

    public ExperienceAdapter(List<Experience> experiences, OnExperienceActionListener listener) {
        this.experiences = experiences;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return experiences.size();
    }

    @Override
    public Experience getItem(int position) {
        return experiences.get(position);
    }

    @Override
    public long getItemId(int position) {
        return experiences.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_experience, parent, false);
        }

        Experience experience = getItem(position);
        ((TextView) view.findViewById(R.id.textInitial)).setText(experience.getTitle().substring(0, 1));
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(experience.getTitle());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(experience.getCity() + " · " + experience.getCategory());
        ((TextView) view.findViewById(R.id.textItemDescription)).setText(experience.getDescription());
        ((TextView) view.findViewById(R.id.textItemMeta)).setText(experience.getDuration() + " · " + experience.getPrice() + " MAD · Rating " + experience.getRating());

        Button detailsButton = view.findViewById(R.id.buttonPrimary);
        detailsButton.setBackgroundTintList((ColorStateList) null);
        detailsButton.setText("View Details");
        detailsButton.setOnClickListener(v -> listener.onDetails(experience));
        view.setOnClickListener(v -> listener.onDetails(experience));
        return view;
    }
}
