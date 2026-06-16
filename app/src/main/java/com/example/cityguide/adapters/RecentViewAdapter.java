package com.example.cityguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.RecentView;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.ImageLoader;

import java.util.List;

public class RecentViewAdapter extends BaseAdapter {

    public interface OnRecentViewActionListener {
        void onOpen(RecentView recentView);
    }

    private final List<RecentView> recentViews;
    private final OnRecentViewActionListener listener;

    public RecentViewAdapter(List<RecentView> recentViews, OnRecentViewActionListener listener) {
        this.recentViews = recentViews;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return recentViews.size();
    }

    @Override
    public RecentView getItem(int position) {
        return recentViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recentViews.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        }

        RecentView recentView = getItem(position);
        int fallback = Constants.FAVORITE_GUIDE.equals(recentView.getItemType())
                ? R.drawable.user_placeholder
                : R.drawable.background_splash;
        ImageLoader.load((ImageView) view.findViewById(R.id.imageFavorite), recentView.getImage(), fallback);
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(recentView.getTitle());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(recentView.getSubtitle());
        ((TextView) view.findViewById(R.id.textItemMeta)).setText(recentView.getItemType().toUpperCase());
        Button removeButton = view.findViewById(R.id.buttonRemove);
        removeButton.setVisibility(View.GONE);
        view.setOnClickListener(v -> listener.onOpen(recentView));
        return view;
    }
}
