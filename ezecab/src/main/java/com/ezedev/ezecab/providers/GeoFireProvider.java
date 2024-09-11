package com.ezedev.ezecab.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeoFireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeoFire;

    public GeoFireProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("active_drivers");
        mGeoFire = new GeoFire(mDatabase);
    }

    public void saveLocation(String driverId, LatLng latLng) {
        mGeoFire.setLocation(driverId, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    public void removeLocation(String driverId) {
        mGeoFire.removeLocation(driverId);
    }

    public GeoQuery getActiveDrivers(LatLng latLng, Double radius) {
        GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius);
        geoQuery.removeAllListeners();
        return geoQuery;
    }

}
