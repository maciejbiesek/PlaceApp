package com.example.maciej.placeapp.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.util.Log;

import com.example.maciej.placeapp.Models.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkProvider {

    private String URL;
    private Location location;
    private Context context;
    private List<Place> placeList;
    private HttpURLConnection conn;
    public int status;

    public NetworkProvider(String URL, Context context, Location location) {
        this.URL = URL;
        this.location = location;
        this.context = context;
        this.placeList = new ArrayList<>();
    }

    public void initConnection() throws IOException {
        java.net.URL url = new java.net.URL(URL);
        this.conn = (HttpURLConnection) url.openConnection();
        this.conn.setReadTimeout(10000 /* milliseconds */);
        this.conn.setConnectTimeout(15000 /* milliseconds */);
        this.conn.setRequestMethod("GET");
        this.conn.setDoInput(true);
        this.conn.connect();

        this.status = conn.getResponseCode();
    }

    public void getPlaces() throws IOException, JSONException {
        String s = downloadFromUrl();
        JSONObject jsonRequest = new JSONObject(s);
        JSONArray jsonResults = jsonRequest.getJSONArray("results");
        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject jsonPlace = jsonResults.getJSONObject(i);
            String name = jsonPlace.getString("name");
            double lat = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            double lng = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            AddressHelper helper = new AddressHelper(this.context);
            String address = helper.getAddress(lat, lng);

            String photoReference;
            try {
                photoReference = jsonPlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            } catch (JSONException e){
                photoReference = null;
            }

            Place place = null;
            if (photoReference != null) {
                place = new Place(photoReference, name, lat, lng, address);
            }
            else {
                place = new Place(name, lat, lng, address);
            }

            if (place != null) {
                Location placeLocation = new Location("placeLocation");
                placeLocation.setLatitude(place.getLat());
                placeLocation.setLongitude(place.getLng());

                double distance = location.distanceTo(placeLocation) / 1000; // change metres to km
                place.setDistance(Math.round(distance * 100) / 100.0);
                placeList.add(place);
            }
        }
        Collections.sort(placeList, new Comparator<Place>() {
            public int compare(Place p1, Place p2) {
                return p1.getDistance().compareTo(p2.getDistance());
            }
        });
    }


    private String downloadFromUrl() throws IOException {
        InputStream is = conn.getInputStream();

        return readStream(is);
    }

    public String readStream(InputStream stream) throws IOException {
        StringBuilder total = new StringBuilder();

        if (stream != null) {
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            stream.close();
        }

        return total.toString();
    }

    public List<Place> getPlaceList() { return placeList; }
}