package com.example.maciej.placeapp.Models;

import java.io.Serializable;

import static com.example.maciej.placeapp.Models.Constants.*;

public class Place implements Serializable {
    private String photoUrl;
    private String name;
    private double lat;
    private double lng;
    private String address;
    private double distance;

    public Place(String photoReference, String name, double lat, double lng, String address) {
        this.photoUrl = Constants.PHOTOS_URL +
                "?maxwidth=" +
                MAX_WIDTH +
                "&photoreference=" +
                photoReference +
                "&key=" +
                Constants.PLACES_API_KEY;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public Place(String name, double lat, double lng, String address) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }


    public String getPhotoUrl() { return photoUrl; }
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getAddress() { return address; }
    public Double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }
}