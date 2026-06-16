package com.example.cityguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.Favorite;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.ImageLoader;

import java.util.List;

public class FavoriteAdapter extends BaseAdapter {

    public interface OnFavoriteActionListener {
        void onRemove(Favorite favorite);
        void onOpen(Favorite favorite);
    }

    private final List<Favorite> favorites;
    private final OnFavoriteActionListener listener;

    public FavoriteAdapter(List<Favorite> favorites, OnFavoriteActionListener listener) {
        this.favorites = favorites;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return favorites.size();
    }

    @Override
    public Favorite getItem(int position) {
        return favorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return favorites.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        }
        Favorite favorite = getItem(position);
        int fallback = Constants.FAVORITE_GUIDE.equals(favorite.getItemType())
                ? R.drawable.user_placeholder
                : R.drawable.background_splash;
        ImageLoader.load((ImageView) view.findViewById(R.id.imageFavorite), favorite.getImage(), fallback);
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(favorite.getTitle());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(favorite.getSubtitle());
        ((TextView) view.findViewById(R.id.textItemMeta)).setText(favorite.getItemType().toUpperCase());
        Button removeButton = view.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(v -> listener.onRemove(favorite));
        view.setOnClickListener(v -> listener.onOpen(favorite));
        return view;
    }
}
