package com.example.cityguide.models;

public class Experience {
    private int id;
    private String title;
    private String city;
    private String category;
    private String description;
    private String duration;
    private double price;
    private String image;
    private double rating;

    public Experience() {
    }

    public Experience(int id, String title, String city, String category, String description,
                      String duration, double price, String image, double rating) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.category = category;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.image = image;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
