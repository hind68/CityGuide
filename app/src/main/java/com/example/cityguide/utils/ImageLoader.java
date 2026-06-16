package com.example.cityguide.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.example.cityguide.R;

public final class ImageLoader {

    private ImageLoader() {
    }

    public static void load(ImageView imageView, String imageName) {
        load(imageView, imageName, R.drawable.background_splash);
    }

    public static void load(ImageView imageView, String imageName, int fallbackResId) {
        if (imageView == null) {
            return;
        }

        Context context = imageView.getContext();
        int resId = 0;
        if (imageName != null && !imageName.trim().isEmpty()) {
            String cleanName = imageName.trim();
            int dotIndex = cleanName.lastIndexOf('.');
            if (dotIndex > 0) {
                cleanName = cleanName.substring(0, dotIndex);
            }
            resId = context.getResources().getIdentifier(cleanName, "drawable", context.getPackageName());
        }

        if (resId != 0) {
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(resId);
            return;
        }

        if (fallbackResId == R.drawable.user_placeholder) {
            imageView.setBackgroundColor(Color.rgb(241, 241, 241));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        imageView.setImageResource(fallbackResId);
    }
}
