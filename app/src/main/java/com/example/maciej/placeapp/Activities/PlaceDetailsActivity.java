package com.example.maciej.placeapp.Activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maciej.placeapp.Models.Place;
import com.example.maciej.placeapp.R;
import com.squareup.picasso.Picasso;

import static com.example.maciej.placeapp.Models.Constants.*;

public class PlaceDetailsActivity extends AppCompatActivity {

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_place_details);

        Intent i = getIntent();
        place = (Place) i.getExtras().getSerializable(PLACE);

        showPlace(place);
        initFooter();
    }

    private void showPlace(Place place) {
        TextView name = (TextView) findViewById(R.id.place_details_name);
        ImageView photo = (ImageView) findViewById(R.id.place_details_photo);
        TextView address = (TextView) findViewById(R.id.place_details_address);
        TextView distance = (TextView) findViewById(R.id.place_details_distance);

        name.setText(place.getName());
        address.setText(place.getAddress());
        distance.setText(String.valueOf(place.getDistance()) + " km");

        Picasso.with(this).load(place.getPhotoUrl()).into(photo);
    }

    private void initFooter() {
        RelativeLayout sendButton = (RelativeLayout) findViewById(R.id.send_email);
        sendButton.setOnClickListener(onClickListener);

        RelativeLayout navigateButton = (RelativeLayout) findViewById(R.id.navigate_to);
        navigateButton.setOnClickListener(onClickListener);

        RelativeLayout googleButton = (RelativeLayout) findViewById(R.id.google_it);
        googleButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.send_email: {
                    sendEmail();
                    break;
                }
                case R.id.navigate_to: {
                    navigateTo();
                    break;
                }
                case R.id.google_it: {
                    searchGoogle();
                    break;
                }
            }
        }
    };

    private void showNoMailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.no_mail_dialog_msg)
                .setTitle(R.string.no_mail_dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

    private void sendEmail() {
        String messageMail =
                getString(R.string.mail_msg1) + " " + place.getName() + ".<br />" +
                getString(R.string.mail_msg2) + " " + place.getAddress() + ".<br /><br />" +
                getString(R.string.mail_msg3);

        String msg = "<html><body><p>" +
                messageMail +
                "</p></body></html>";

        Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setType("text/html")
                .setData(new Uri.Builder().scheme("mailto").build())
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_title))
                .putExtra(Intent.EXTRA_TEXT, Html.fromHtml(msg));

        if (isEmailSetup()) {
            Intent chooser = Intent.createChooser(intent, getString(R.string.sync_choose_app));
            startActivity(chooser);
        }
        else showNoMailDialog();
    }

    private boolean isEmailSetup() {
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        return accounts.length > 0;
    }

    private void searchGoogle() {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, place.getName());
        startActivity(intent);
    }

    private void navigateTo() {
        String uri = "google.navigation:q=" + String.valueOf(place.getLat()) + "," + String.valueOf(place.getLng());
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mapsIntent);
    }

}
