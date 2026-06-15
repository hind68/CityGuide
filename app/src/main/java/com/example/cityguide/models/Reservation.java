package com.example.cityguide.models;

public class Reservation {
    private int id;
    private int userId;
    private int guideId;
    private int experienceId;
    private String title;
    private String date;
    private String time;
    private int numberOfHours;
    private double totalPrice;
    private String message;
    private String status;

    public Reservation() {
    }

    public Reservation(int id, int userId, String title, String date, String status) {
        this(id, userId, -1, -1, title, date, "", 1, 0, "", status);
    }

    public Reservation(int id, int userId, int guideId, int experienceId, String title,
                       String date, String time, int numberOfHours, double totalPrice,
                       String message, String status) {
        this.id = id;
        this.userId = userId;
        this.guideId = guideId;
        this.experienceId = experienceId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.numberOfHours = numberOfHours;
        this.totalPrice = totalPrice;
        this.message = message;
        this.status = status;
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

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public int getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(int experienceId) {
        this.experienceId = experienceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumberOfHours() {
        return numberOfHours;
    }

    public void setNumberOfHours(int numberOfHours) {
        this.numberOfHours = numberOfHours;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
