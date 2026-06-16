package com.example.cityguide.network;

public class RemotePlaceDto {
    public int id;
    public String name;
    public String city;
    public String category;
    public double latitude;
    public double longitude;

    public RemotePlaceDto(int id, String name, String city, String category,
                          double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
