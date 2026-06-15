package com.example.cityguide.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class IntentUtils {

    private IntentUtils() {
    }

    public static void openActivity(Context context, Class<?> activityClass) {
        context.startActivity(new Intent(context, activityClass));
    }

    public static void openMap(Context context, double latitude, double longitude, String label) {
        String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude
                + "(" + Uri.encode(label) + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }

    public static void callPhone(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    public static void sendEmail(Context context, String email, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        context.startActivity(intent);
    }

    public static void shareText(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "Share with"));
    }
}
