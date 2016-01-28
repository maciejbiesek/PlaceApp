package com.example.maciej.placeapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.example.maciej.placeapp.R;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import static com.example.maciej.placeapp.Models.Constants.*;


public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Profile user;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initialize() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logOut();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        startMapActivity();
                    }

                    @Override
                    public void onCancel() {
                        Log.i("DEBUG", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i("DEBUG", "error");
                    }
                });

        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    startMapActivity();
                }
                else {
                    showUserDialog(getString(R.string.internet_problem), getString(R.string.internet_not_found));
                }
            }
        });
    }

    private void startMapActivity() {
        Intent i = new Intent(LoginActivity.this, MapActivity.class);
        if (isLoggedIn()) {
            user = Profile.getCurrentProfile();
            String id = user.getId();
            String name = user.getName();
            String photoUri = String.valueOf(user.getProfilePictureUri(100, 100));
            i.putExtra(USER_ID, id);
            i.putExtra(USER_NAME, name);
            i.putExtra(USER_PHOTO, photoUri);
        }

        startActivity(i);
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

    private boolean isLoggedIn() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!isOnline()) {
            showUserDialog(getString(R.string.internet_problem), getString(R.string.internet_not_found));
        }
    }
}
