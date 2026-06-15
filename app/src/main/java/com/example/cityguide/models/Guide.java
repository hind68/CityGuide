package com.example.cityguide.models;

public class Guide {
    private int id;
    private String name;
    private String city;
    private String languages;
    private String specialty;
    private double pricePerHour;
    private String phone;
    private String email;
    private String image;
    private double rating;
    private String description;

    public Guide() {
    }

    public Guide(int id, String name, String city, String languages, String specialty,
                 double pricePerHour, String phone, String email, String image,
                 double rating, String description) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.languages = languages;
        this.specialty = specialty;
        this.pricePerHour = pricePerHour;
        this.phone = phone;
        this.email = email;
        this.image = image;
        this.rating = rating;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
