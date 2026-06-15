package com.example.cityguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cityguide.R;
import com.example.cityguide.models.Favorite;

import java.util.List;

public class FavoriteAdapter extends BaseAdapter {

    private final List<Favorite> favorites;

    public FavoriteAdapter(List<Favorite> favorites) {
        this.favorites = favorites;
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
        ((TextView) view.findViewById(R.id.textItemTitle)).setText(favorite.getTitle());
        ((TextView) view.findViewById(R.id.textItemSubtitle)).setText(favorite.getSubtitle());
        ((TextView) view.findViewById(R.id.textItemMeta)).setText(favorite.getItemType());
        return view;
    }
}
