package com.example.cityguide.models;

public class SavedItinerary {
    private final int id;
    private final int userId;
    private final int placeId;
    private final String title;
    private final String subtitle;
    private final String image;
    private final double latitude;
    private final double longitude;

    public SavedItinerary(int id, int userId, int placeId, String title, String subtitle,
                          String image, double latitude, double longitude) {
        this.id = id;
        this.userId = userId;
        this.placeId = placeId;
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getPlaceId() {
        return placeId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImage() {
        return image;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
