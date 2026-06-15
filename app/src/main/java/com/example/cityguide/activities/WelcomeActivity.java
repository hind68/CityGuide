package com.example.cityguide.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.example.cityguide.R;
import com.example.cityguide.adapters.OnboardingAdapter;
import com.example.cityguide.models.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private static final int ACTIVE_DOT_COLOR = Color.parseColor("#0B2E63");
    private static final int INACTIVE_DOT_COLOR = Color.parseColor("#66C6A15B");

    private ViewPager2 onboardingViewPager;
    private LinearLayout dotsLayout;
    private Button buttonNext;
    private TextView textSkip;
    private List<OnboardingItem> onboardingItems;

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        onboardingViewPager = findViewById(R.id.onboardingViewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        buttonNext = findViewById(R.id.buttonNext);
        textSkip = findViewById(R.id.textSkip);

        onboardingItems = createOnboardingItems();
        onboardingViewPager.setAdapter(new OnboardingAdapter(onboardingItems));

        setupDots();
        updateDots(0);
        updateButtonText(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
                updateButtonText(position);
            }
        });

        buttonNext.setOnClickListener(v -> {
            int current = onboardingViewPager.getCurrentItem();
            if (current < onboardingItems.size() - 1) {
                onboardingViewPager.setCurrentItem(current + 1, true);
            } else {
                openAndFinish(SignInActivity.class);
            }
        });
        textSkip.setOnClickListener(v -> openAndFinish(SignInActivity.class));
    }

    private List<OnboardingItem> createOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(
                R.drawable.welcome_hassan,
                "Iconic Morocco",
                "Discover grand monuments, timeless architecture and unforgettable city landmarks."));
        items.add(new OnboardingItem(
                R.drawable.welcome_koutoubia,
                "Explore Around You",
                "Find nearby places on the map, plan beautiful stops and move through each city with confidence."));
        items.add(new OnboardingItem(
                R.drawable.welcome_chef,
                "Guides & Hidden Gems",
                "Meet local guides, save favorite places and discover experiences you would not find alone."));
        return items;
    }

    private void setupDots() {
        dotsLayout.removeAllViews();
        for (int i = 0; i < onboardingItems.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(9), dpToPx(9));
            params.setMargins(dpToPx(5), 0, dpToPx(5), 0);
            dot.setLayoutParams(params);
            dotsLayout.addView(dot);
        }
    }

    private void updateDots(int activePosition) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            View dot = dotsLayout.getChildAt(i);
            boolean isActive = i == activePosition;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(isActive ? 24 : 9),
                    dpToPx(9)
            );
            params.setMargins(dpToPx(5), 0, dpToPx(5), 0);
            dot.setLayoutParams(params);
            dot.setBackground(createDotDrawable(isActive));
        }
    }

    private GradientDrawable createDotDrawable(boolean active) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dpToPx(8));
        drawable.setColor(active ? ACTIVE_DOT_COLOR : INACTIVE_DOT_COLOR);
        return drawable;
    }

    private void updateButtonText(int position) {
        buttonNext.setText(position == onboardingItems.size() - 1 ? "Get Started" : "Next");
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void openAndFinish(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
        finish();
    }
}
