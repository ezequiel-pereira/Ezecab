package com.ezedev.ezecab.providers;

import android.content.Context;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.retrofit.IDirectionsAPI;
import com.ezedev.ezecab.retrofit.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;

public class DirectionsProvider {
    private Context context;
    public DirectionsProvider(Context coso) {
        context = coso;
    }

    public Call<String> getDirections(LatLng origin, LatLng destination) {
        String basUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + origin.latitude + "," + origin.longitude + "&"
                + "destination=" + destination.latitude + "," + destination.longitude + "&"
                + "key=" + context.getResources().getString(R.string.google_maps_apikey);
        return RetrofitClient.getClient(basUrl).create(IDirectionsAPI.class).getDirections(basUrl+query);
    }
}
