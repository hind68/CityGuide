package com.example.cityguide.models;

public class RecentView {
    private final int id;
    private final int userId;
    private final int itemId;
    private final String itemType;
    private final String title;
    private final String subtitle;
    private final String image;

    public RecentView(int id, int userId, int itemId, String itemType,
                      String title, String subtitle, String image) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemType() {
        return itemType;
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
}
