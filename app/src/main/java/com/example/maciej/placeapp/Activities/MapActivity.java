package com.example.maciej.placeapp.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maciej.placeapp.Helpers.AddressHelper;
import com.example.maciej.placeapp.Helpers.NetworkProvider;
import com.example.maciej.placeapp.Models.Constants;
import com.example.maciej.placeapp.Models.Place;
import com.example.maciej.placeapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.maciej.placeapp.Models.Constants.*;


public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;

    private Button showButton;

    private String URL;
    private Location userLocation;
    private Double userLatitude;
    private Double userLongitude;
    private List<Place> placeList;
    private boolean isFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_map);

        Intent i = getIntent();
        bindFooter(i);
        initialize();
    }

    private void initialize() {
        placeList = new ArrayList<Place>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showButton = (Button) findViewById(R.id.show_button);
        showButton.setVisibility(View.INVISIBLE);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapActivity.this, PlaceListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(PLACE_LIST, (Serializable) placeList);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        AddressHelper helper = new AddressHelper(this);

        Log.i("debug", "Location services connected.");
        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        userLatitude = userLocation.getLatitude();
        userLongitude = userLocation.getLongitude();
        String address = helper.getAddress(userLatitude, userLongitude);
        plotMarker(address);

        URL = Constants.PLACES_URL + "?location=" + userLatitude + "," + userLongitude + "&radius=" + RADIUS
                + "&key=" + Constants.PLACES_API_KEY;

        (new AsyncPlacesDownload()).execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("debug", "Location services suspended. Please reconnect.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("debug", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("debug", "Map ready.");
        googleMap = map;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            userLatitude = userLocation.getLatitude();
            userLongitude = userLocation.getLongitude();
        }
    }

    private void plotMarker(String address) {
        LatLng currentLocation = new LatLng(userLatitude, userLongitude);
        googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title(address)
                .snippet(getString(R.string.location_here)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 11));
    }

    private void plotMarkers(List<Place> places) {
        for (Place place : places) {
            LatLng location = new LatLng(place.getLat(), place.getLng());
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(place.getAddress()));
        }
    }

    private void bindFooter(Intent intent) {
        TextView fbName = (TextView) findViewById(R.id.fb_name);
        ImageView fbPhoto = (ImageView) findViewById(R.id.fb_photo);
        String userInfo = "";

        if (intent.getExtras() != null) {
            userInfo = getString(R.string.logged_as) + " " + intent.getStringExtra(USER_NAME);
            String userPhotoUrl = intent.getStringExtra(USER_PHOTO);
            Picasso.with(this).load(userPhotoUrl).into(fbPhoto);
        }
        else {
            userInfo = getString(R.string.not_logged);
        }

        fbName.setText(userInfo);
    }

    private void showUserDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }



    private class AsyncPlacesDownload extends AsyncTask<String, Void, List<Place>> {

        private NetworkProvider networkProvider;

        @Override
        protected void onPostExecute(List<Place> result) {
            super.onPostExecute(result);
            if (!result.isEmpty()) {
                placeList.clear();
                placeList.addAll(result);
                isFinished = true;
                showButton.setVisibility(View.VISIBLE);
                plotMarkers(placeList);
            }
            else showUserDialog(getString(R.string.server_connection), getString(R.string.server_fail));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            networkProvider = new NetworkProvider(URL, MapActivity.this, userLocation);
            isFinished = false;
        }

        @Override
        protected List<Place> doInBackground(String... params) {
            try {
                networkProvider.initConnection();
                if (networkProvider.status == 200) {
                    networkProvider.getPlaces();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return networkProvider.getPlaceList();
        }
    }
}