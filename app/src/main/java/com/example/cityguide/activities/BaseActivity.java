package com.example.cityguide.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cityguide.R;

public class BaseActivity extends AppCompatActivity {

    private static final int NAV_HEIGHT_DP = 88;
    private boolean bottomNavigationInstalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableImmersiveMode();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        clearButtonTints(findViewById(android.R.id.content));
        if (shouldShowBottomNavigation()) {
            installBottomNavigation();
        }
    }

    protected boolean shouldShowBottomNavigation() {
        return true;
    }

    protected void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void installBottomNavigation() {
        if (bottomNavigationInstalled) {
            return;
        }

        FrameLayout content = findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) {
            return;
        }

        View page = content.getChildAt(0);
        page.setPadding(
                page.getPaddingLeft(),
                page.getPaddingTop() + dp(22),
                page.getPaddingRight(),
                page.getPaddingBottom() + dp(NAV_HEIGHT_DP)
        );

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(dp(10));
        nav.setPadding(dp(2), dp(5), dp(2), dp(6));

        nav.addView(createNavItem("Home", R.drawable.ic_home, HomeActivity.class, this instanceof HomeActivity));
        nav.addView(createNavItem("Maps", R.drawable.ic_map, MapActivity.class, this instanceof MapActivity));
        nav.addView(createNavItem("Guides", R.drawable.ic_guide, GuidesActivity.class, this instanceof GuidesActivity));
        nav.addView(createNavItem("Favorites", R.drawable.ic_favorite, FavoritesActivity.class, this instanceof FavoritesActivity));
        nav.addView(createNavItem("Profile", R.drawable.ic_settings, SettingsActivity.class, this instanceof SettingsActivity));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(NAV_HEIGHT_DP),
                Gravity.BOTTOM
        );
        content.addView(nav, params);
        bottomNavigationInstalled = true;
    }

    private LinearLayout createNavItem(String label, int iconRes, Class<?> activityClass, boolean active) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(0, dp(2), 0, dp(2));
        item.setClickable(true);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        item.setLayoutParams(itemParams);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setColorFilter(active ? Color.parseColor("#0B2E63") : Color.parseColor("#8A8F98"));
        if (active) {
            GradientDrawable iconBackground = new GradientDrawable();
            iconBackground.setShape(GradientDrawable.OVAL);
            iconBackground.setColor(Color.parseColor("#190B2E63"));
            icon.setBackground(iconBackground);
            icon.setPadding(dp(8), dp(8), dp(8), dp(8));
        }
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(active ? 50 : 34), dp(active ? 50 : 34));
        icon.setLayoutParams(iconParams);

        TextView text = new TextView(this);
        text.setText(label);
        text.setGravity(Gravity.CENTER);
        text.setTextSize(active ? 11 : 10);
        text.setSingleLine(true);
        text.setTextColor(active ? Color.parseColor("#0B2E63") : Color.parseColor("#8A8F98"));

        item.addView(icon);
        item.addView(text);
        item.setOnClickListener(v -> {
            if (!active) {
                Intent intent = new Intent(this, activityClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        return item;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void clearButtonTints(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof Button) {
            ((Button) view).setBackgroundTintList((ColorStateList) null);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                clearButtonTints(group.getChildAt(i));
            }
        }
    }
}
