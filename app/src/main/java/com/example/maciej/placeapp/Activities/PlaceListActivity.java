package com.example.maciej.placeapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.maciej.placeapp.Adapters.PlaceAdapter;
import com.example.maciej.placeapp.Models.Place;
import com.example.maciej.placeapp.R;

import java.util.List;

import static com.example.maciej.placeapp.Models.Constants.*;


public class PlaceListActivity extends AppCompatActivity {

    private PlaceAdapter mAdapter;
    private ListView placeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_place_list);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        List<Place> places = (List<Place>) bundle.getSerializable(PLACE_LIST);

        mAdapter = new PlaceAdapter(this, places);

        placeListView = (ListView) findViewById(R.id.list_view);
        placeListView.setAdapter(mAdapter);
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Place place = mAdapter.getItem(position);

                Intent i = new Intent(PlaceListActivity.this, PlaceDetailsActivity.class);
                i.putExtra(PLACE, place);
                startActivity(i);
            }
        });
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}



