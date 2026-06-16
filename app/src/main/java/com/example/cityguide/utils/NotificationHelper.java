package com.example.cityguide.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.cityguide.R;
import com.example.cityguide.activities.PlaceDetailsActivity;
import com.example.cityguide.models.Place;

public final class NotificationHelper {
    public static final String CHANNEL_ID = "nearby_suggestions";

    private NotificationHelper() {
    }

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Nearby suggestions",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Suggestions based on your current position.");
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public static void notifyNearbyPlace(Context context, Place place) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, PlaceDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_PLACE_ID, place.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                place.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_place)
                .setContentTitle("A place is waiting nearby")
                .setContentText(place.getName() + " in " + place.getCity() + " is your closest CityGuide pick.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(place.getName() + " in " + place.getCity()
                                + " is your closest CityGuide pick. Tap to see photos, details and the route."))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#0B2E63"))
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(context).notify(1000 + place.getId(), builder.build());
    }
}
