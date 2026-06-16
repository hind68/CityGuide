package com.example.cityguide.models;

public class Favorite {
    private int id;
    private int userId;
    private int itemId;
    private String itemType;
    private String title;
    private String subtitle;
    private String image;

    public Favorite() {
    }

    public Favorite(int id, int userId, int itemId, String itemType) {
        this(id, userId, itemId, itemType, "", "", "");
    }

    public Favorite(int id, int userId, int itemId, String itemType, String title, String subtitle) {
        this(id, userId, itemId, itemType, title, subtitle, "");
    }

    public Favorite(int id, int userId, int itemId, String itemType, String title, String subtitle, String image) {
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

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
