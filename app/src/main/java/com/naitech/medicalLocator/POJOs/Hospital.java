package com.naitech.medicalLocator.POJOs;

public class Hospital {
    private String name;
    private double latitude;
    private double longitude;

    public Hospital() {
    }

    public Hospital(String hospital_name, double latitude, double longitude) {
        this.name = hospital_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String hospital_name) {
        this.name= hospital_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "hospital_name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
