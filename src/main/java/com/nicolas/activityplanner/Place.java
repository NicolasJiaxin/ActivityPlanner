package com.nicolas.activityplanner;

public class Place {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private int visitDuration;

    public Place(int id, String name, double latitude, double longitude, int visitDuration) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visitDuration = visitDuration;
    }

//    public Place(double latitude, double longitude, int visitDuration) {
//        this.name = "";
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.visitDuration = visitDuration;
//    }
//
//    public Place(double latitude, double longitude) {
//        this.name = "";
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.visitDuration = 0;
//    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getVisitDuration() {
        return visitDuration;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", visitDuration=" + visitDuration +
                '}';
    }
}
