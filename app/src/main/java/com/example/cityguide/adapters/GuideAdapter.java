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
import com.example.cityguide.models.Guide;
import com.example.cityguide.utils.ImageLoader;

import java.util.List;

public class GuideAdapter extends BaseAdapter {

    public interface OnGuideActionListener {
        void onProfile(Guide guide);
    }

    private final List<Guide> guides;
    private final OnGuideActionListener listener;

    public GuideAdapter(List<Guide> guides, OnGuideActionListener listener) {
        this.guides = guides;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return guides.size();
    }

    @Override
    public Guide getItem(int position) {
        return guides.get(position);
    }

    @Override
    public long getItemId(int position) {
        return guides.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guide, parent, false);
        }

        Guide guide = getItem(position);
        ImageLoader.load((ImageView) view.findViewById(R.id.imageGuide), guide.getImage(), R.drawable.user_placeholder);
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(guide.getName());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(guide.getCity() + " - " + guide.getSpecialty());
        ((TextView) view.findViewById(R.id.textItemDescription)).setText(guide.getLanguages());
        ((TextView) view.findViewById(R.id.textItemMeta)).setText(guide.getPricePerHour() + " MAD/hour - Rating " + guide.getRating());

        Button profileButton = view.findViewById(R.id.buttonPrimary);
        profileButton.setBackgroundTintList((ColorStateList) null);
        profileButton.setText("View Profile");
        profileButton.setOnClickListener(v -> listener.onProfile(guide));
        view.setOnClickListener(v -> listener.onProfile(guide));
        return view;
    }
}
