package com.ezedev.ezecab.activities.passenger;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.providers.GeoFireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;

public class RequestActivity extends AppCompatActivity {
    private TextView mTextViewSearch;
    private Button mBtnCancel;
    private GeoFireProvider mGeofireProvider;
    private LatLng mOrigin;
    private Double mSearchRadius = 5.0;
    private Boolean mDriverFound =  false;
    private String mDriverId;
    private LatLng mDriverLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mTextViewSearch = findViewById(R.id.textViewSearch);
        mBtnCancel = findViewById(R.id.btnCancel);
        mOrigin = getIntent().getExtras().getParcelable("origin");

        mGeofireProvider = new GeoFireProvider();

        getClosestDrivers();
    }

    private void getClosestDrivers () {
        mGeofireProvider.getActiveDrivers(mOrigin, mSearchRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation geoLocation) {
                if (!mDriverFound) {
                    mDriverFound = true;
                    mDriverId = s;
                    mDriverLocation = new LatLng(geoLocation.latitude, geoLocation.latitude);
                    mTextViewSearch.setText("Conductor encontrado:\nDriverID: " + mDriverId + "\nDriver LatLng: " + mDriverLocation.latitude + " " + mDriverLocation.longitude);
                }
            }

            @Override
            public void onKeyExited(String s) {

            }

            @Override
            public void onKeyMoved(String s, GeoLocation geoLocation) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!mDriverFound) {
                    mSearchRadius= mSearchRadius + 0.5;

                    if (mSearchRadius > 10) {
                        Toast.makeText(RequestActivity.this, "No pudimos encontrar un conductor", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        getClosestDrivers();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError databaseError) {

            }
        });
    }
}