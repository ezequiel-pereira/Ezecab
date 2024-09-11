package com.ezedev.ezecab.activities.passenger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.providers.AuthProvider;
import com.ezedev.ezecab.providers.GeoFireProvider;
import com.ezedev.ezecab.providers.InstallationTokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PassengerMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final static int LOCATION_REQUEST_CODE = 1;
    private AuthProvider mAuthProvider;
    private GeoFireProvider mGeoFireProvider;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mlocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private Marker mMarker;
    private LatLng mCurrentLatLng;
    private final List<Marker> mDriversMarkers = new ArrayList<>();
    private boolean firstTime = true;
    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutoCompleteOrigin;
    private String mOrigin;
    private LatLng mOriginLatLng;
    private AutocompleteSupportFragment mAutoCompleteDestiantion;
    private String mDestination;
    private LatLng mDestinationLatLng;
    private GoogleMap.OnCameraIdleListener mCameraListener;
    private Button mButtonRequestDriver;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Vos"));
                    //mMarker.setDraggable(true);
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15f).build()));
                    updateLocation();
                    if (firstTime) {
                        firstTime = false;
                        getActiveDrivers();
                        limitAutocompleteSearchResults();
                    }
                }
            }
        }
    };

    private void updateLocation() {
        if (mAuthProvider.sessionExists() && mCurrentLatLng != null)
            mGeoFireProvider.saveLocation(mAuthProvider.getId(), mCurrentLatLng);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);

        //MyToolbar.show(this, "Pasajero", false);

        mAuthProvider = new AuthProvider();
        mGeoFireProvider = new GeoFireProvider();
        tokenProvider = new InstallationTokenProvider();
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

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.passenger_map);
        mMapFragment.getMapAsync(this);
        mButtonRequestDriver = findViewById(R.id.btn_request_driver);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_apikey));
        }

        mPlaces = Places.createClient(this);

        instanceAutocompleteOrigin();
        instanceAutocompleteDestination();
        onCameraMove();

        mButtonRequestDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDriver();
            }
        });

        getInstallationToken();
    }

    private void requestDriver() {
        if (mOriginLatLng != null && mDestinationLatLng != null) {
            Intent intent = new Intent(PassengerMapActivity.this, RequestDetailsActivity.class);
            intent.putExtra("origin", mOriginLatLng);
            intent.putExtra("destination", mDestinationLatLng);
            intent.putExtra("originText", mOrigin);
            intent.putExtra("destinationText", mDestination);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Debe seleccionar origen y destino", Toast.LENGTH_SHORT).show();
        }
    }

    private void instanceAutocompleteOrigin() {
        mAutoCompleteOrigin = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_origin);
        mAutoCompleteOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoCompleteOrigin.setHint("Desde...");
        mAutoCompleteOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getName();
                mOriginLatLng = place.getLatLng();
                Log.d("PLACE", "Name " + mOrigin);
                Log.d("PLACE", "Lat " + mOriginLatLng.latitude);
                Log.d("PLACE", "Lng " + mOriginLatLng.longitude);
            }
        });
    }

    private void instanceAutocompleteDestination() {
        mAutoCompleteDestiantion = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_destination);
        mAutoCompleteDestiantion.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoCompleteDestiantion.setHint("Hacia...");
        mAutoCompleteDestiantion.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDestination = place.getName();
                mDestinationLatLng = place.getLatLng();
                Log.d("PLACE", "Name " + mDestination);
                Log.d("PLACE", "Lat " + mDestinationLatLng.latitude);
                Log.d("PLACE", "Lng " + mDestinationLatLng.longitude);
            }
        });
    }

    private void onCameraMove() {
        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geoCoder = new Geocoder(PassengerMapActivity.this);
                    mOriginLatLng = mMap.getCameraPosition().target;
                    List<Address> addressList = geoCoder.getFromLocation(mOriginLatLng.latitude, mOriginLatLng.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    String country = addressList.get(0).getCountryName();
                    String address = addressList.get(0).getAddressLine(0);
                    mAutoCompleteOrigin.setText(address + " " + city);
                    mOrigin = address + " " + city;
                } catch (Exception exception) {
                    Log.d("Error: ", "msg: " + exception.getMessage());
                }
            }
        };
    }

    private void limitAutocompleteSearchResults() {
        LatLng northSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000, 0);
        LatLng southSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000, 180);
        mAutoCompleteOrigin.setCountry("ARG");
        mAutoCompleteOrigin.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
        mAutoCompleteDestiantion.setCountry("ARG");
        mAutoCompleteDestiantion.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
    }

    private void getActiveDrivers() {
        mGeoFireProvider.getActiveDrivers(mCurrentLatLng, 5.0).addGeoQueryEventListener(new GeoQueryEventListener() {
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
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Conductor").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_uber_car)));
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
        mMap.setOnCameraIdleListener(mCameraListener);

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
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setTitle("Proporcione los permisos apra continuar").setMessage("Esta aplicación requiere permisos de ubicación").setPositiveButton("OK", (dialogInterface, i) -> {
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

    InstallationTokenProvider tokenProvider;
    void getInstallationToken() {
        tokenProvider.create(mAuthProvider.getId());
    }
}