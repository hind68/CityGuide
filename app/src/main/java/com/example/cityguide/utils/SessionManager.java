package com.example.cityguide.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "cityguide_session";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_GUEST = "guest";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_DEFAULT_CITY = "default_city";
    private static final String KEY_DEFAULT_LANGUAGE = "default_language";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final int DEFAULT_USER_ID = -1;

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setGuest(boolean guest) {
        preferences.edit().putBoolean(KEY_GUEST, guest).apply();
    }

    public boolean isGuest() {
        return preferences.getBoolean(KEY_GUEST, false);
    }

    public void saveUserId(int userId) {
        preferences.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, DEFAULT_USER_ID);
    }

    public void saveUserName(String userName) {
        preferences.edit().putString(KEY_USER_NAME, userName).apply();
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "Traveler");
    }

    public void setDarkMode(boolean darkMode) {
        preferences.edit().putBoolean(KEY_DARK_MODE, darkMode).apply();
    }

    public boolean isDarkMode() {
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void saveDefaultCity(String city) {
        preferences.edit().putString(KEY_DEFAULT_CITY, city).apply();
    }

    public String getDefaultCity() {
        return preferences.getString(KEY_DEFAULT_CITY, "Marrakech");
    }

    public void saveDefaultLanguage(String language) {
        preferences.edit().putString(KEY_DEFAULT_LANGUAGE, language).apply();
    }

    public String getDefaultLanguage() {
        return preferences.getString(KEY_DEFAULT_LANGUAGE, "English");
    }

    public void saveLanguageCode(String languageCode) {
        preferences.edit().putString(KEY_DEFAULT_LANGUAGE, languageCode).apply();
    }

    public String getLanguageCode() {
        String value = preferences.getString(KEY_DEFAULT_LANGUAGE, "en");
        if ("French".equalsIgnoreCase(value)) {
            return "fr";
        }
        if ("English".equalsIgnoreCase(value)) {
            return "en";
        }
        if ("fr".equalsIgnoreCase(value)) {
            return "fr";
        }
        return "en";
    }

    public void setFirstLaunch(boolean firstLaunch) {
        preferences.edit().putBoolean(KEY_FIRST_LAUNCH, firstLaunch).apply();
    }

    public boolean isFirstLaunch() {
        return preferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void logout() {
        preferences.edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .putBoolean(KEY_GUEST, false)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_NAME)
                .apply();
    }
}
