package com.example.cityguide.models;

public class TourismOffice {
    private final String name;
    private final String city;
    private final String phone;
    private final String smsNumber;
    private final String address;

    public TourismOffice(String name, String city, String phone, String smsNumber, String address) {
        this.name = name;
        this.city = city;
        this.phone = phone;
        this.smsNumber = smsNumber;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public String getAddress() {
        return address;
    }
}
