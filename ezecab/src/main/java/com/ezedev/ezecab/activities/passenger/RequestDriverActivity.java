package com.ezedev.ezecab.activities.passenger;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ezedev.ezecab.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class RequestDriverActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LatLng mOrigin;
    private LatLng mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_driver);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.passenger_map);
        mMapFragment.getMapAsync(this);

        mOrigin = getIntent().getExtras().getParcelable("origin");
        mDestination = getIntent().getExtras().getParcelable("destination");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(mOrigin));
        mMap.addMarker(new MarkerOptions().position(mDestination));

        centerMapCameraOnRoute();

        /*mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mOrigin)
                        .zoom(14f)
                        .build()));*/
    }

    private void centerMapCameraOnRoute() {
        Double southernLatitude;
        Double southernLongitude;
        Double northernLatitude;
        Double northernLongitude;

        if (mOrigin.latitude < mDestination.latitude) {
            southernLatitude = mOrigin.latitude;
            northernLatitude = mDestination.latitude;
        } else {
            southernLatitude = mDestination.latitude;
            northernLatitude = mOrigin.latitude;
        }

        if (mOrigin.longitude < mDestination.longitude) {
            southernLongitude = mOrigin.longitude;
            northernLongitude = mDestination.longitude;
        } else {
            southernLongitude = mDestination.longitude;
            northernLongitude = mOrigin.longitude;
        }
        Log.d("tag", "south " + southernLatitude + "   " + southernLongitude);
        Log.d("tag", "north" + northernLatitude + "   " + northernLongitude);

        LatLngBounds routeBounds = new LatLngBounds(
                new LatLng(southernLatitude, southernLongitude),
                new LatLng(northernLatitude, northernLongitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routeBounds.getCenter(), 14));
    }
}