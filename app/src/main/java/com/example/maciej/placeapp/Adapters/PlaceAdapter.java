package com.example.maciej.placeapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maciej.placeapp.Models.Place;
import com.example.maciej.placeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PlaceAdapter extends BaseAdapter {
    private List<Place> places;
    private Context context;

    public PlaceAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }

    public void setPlaces(List<Place> placeList) {
        places.clear();
        places.addAll(placeList);
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Place getItem(int i) {
        return places.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View placeView;

        if (convertView == null) {
            placeView = LayoutInflater.from(context).inflate(R.layout.place_row, parent, false);
        } else {
            placeView = convertView;
        }

        bindPlaceToView(getItem(i), placeView);

        return placeView;
    }

    private void bindPlaceToView(Place place, View placeView) {
        ImageView placePhoto = (ImageView) placeView.findViewById(R.id.place_photo);
        TextView placeName = (TextView) placeView.findViewById(R.id.place_name);
        TextView placeAddress = (TextView) placeView.findViewById(R.id.place_address);
        TextView placeDistance = (TextView) placeView.findViewById(R.id.place_distance);

        Picasso.with(context).load(place.getPhotoUrl()).into(placePhoto);
        placeName.setText(place.getName());
        placeAddress.setText(place.getAddress());

        placeDistance.setText(String.valueOf(place.getDistance()) + " km");
    }

}