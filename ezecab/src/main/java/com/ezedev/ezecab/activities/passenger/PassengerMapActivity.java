package com.ezedev.ezecab.activities.passenger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.providers.AuthProvider;
import com.ezedev.ezecab.providers.GeoFireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class PassengerMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final static int LOCATION_REQUEST_CODE = 1;
    Button mButtonLogout;
    private AuthProvider mAuthProvider;
    private GeoFireProvider mGeoFireProvider;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mlocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private Marker mMarker;

    private boolean firstTime;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    if (mMarker != null) {
                        mMarker.remove();
                    }

                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Vos")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location))
                    );

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));

                    if (firstTime) {
                        getActiveDrivers();
                        firstTime = false;
                    }
                }
            }
        }
    };
    private LatLng mCurrentLatLng;
    private final List<Marker> mDriversMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);

        //mButtonLogout = findViewById(R.id.btnLogout);
        mAuthProvider = new AuthProvider();
        /*mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthProvider.logout();
                Intent intent = new Intent(PassengerMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_map);
        mMapFragment.getMapAsync(this);
    }

    private void getActiveDrivers() {
        mGeoFireProvider.getActiveDrivers(mCurrentLatLng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for (Marker marker : mDriversMarkers) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            return;
                        }
                    }
                }

                LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatLng)
                        .title("Conductor")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_uber_car)));
                marker.setTag(key);
                mDriversMarkers.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker : mDriversMarkers) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.remove();
                            mDriversMarkers.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker marker : mDriversMarkers) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mlocationRequest = new com.google.android.gms.location.LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocation.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
                }
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporcione los permisos apra continuar")
                        .setMessage("Esta aplicación requiere permisos de ubicación")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            Toast.makeText(PassengerMapActivity.this, "check location permission", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(PassengerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                        });
            } else {
                ActivityCompat.requestPermissions(PassengerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocation.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                checkLocationPermission();
            }
        } else {
            mFusedLocation.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
        }
    }
}