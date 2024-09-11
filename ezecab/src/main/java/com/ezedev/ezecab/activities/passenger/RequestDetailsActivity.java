package com.ezedev.ezecab.activities.passenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.providers.DirectionsProvider;
import com.ezedev.ezecab.utils.GeoDecode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LatLng mOrigin;
    private LatLng mDestination;
    private DirectionsProvider mDirectionsProvider;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;
    private TextView mTexViewOrigin;
    private TextView mTexViewDestination;
    private TextView mTexViewTime;
    private TextView mTexViewDistance;
    private String originText;
    private String destinationText;
    private String timeText;
    private String distanceText;
    private View mRequestButton;

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

        mTexViewOrigin = findViewById(R.id.textViewOriginContent);
        mTexViewDestination = findViewById(R.id.textViewDestinationContent);
        mTexViewTime = findViewById(R.id.textViewTime);
        mTexViewDistance = findViewById(R.id.textViewDistance);
        mRequestButton = findViewById(R.id.btnRequest);

        mDirectionsProvider = new DirectionsProvider(RequestDetailsActivity.this);

        mOrigin = getIntent().getExtras().getParcelable("origin");
        mDestination = getIntent().getExtras().getParcelable("destination");

        originText = getIntent().getStringExtra("originText");
        destinationText = getIntent().getStringExtra("destinationText");
        mTexViewOrigin.setText(originText);
        mTexViewDestination.setText(destinationText);

        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requstButton();
            }
        });
    }

    private void requstButton() {
        Intent intent = new Intent(RequestDetailsActivity.this, RequestActivity.class);
        intent.putExtra("origin", mOrigin);
        //intent.putExtra("destination", mDestination);
        startActivity(intent);
        finish();
    }

    private void drawRoute() {
        mDirectionsProvider.getDirections(mOrigin, mDestination).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject json = new JSONObject(response.body());
                    JSONArray array = json.getJSONArray("routes");
                    JSONObject route = array.getJSONObject(0);
                    JSONObject polyline = route.getJSONObject("overview_polyline");
                    String points = polyline.getString("points");
                    mPolylineList = GeoDecode.decode(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(8f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    JSONArray legs = route.getJSONArray("legs");
                    Log.d("Log legs", legs.toString());
                    JSONObject leg = legs.getJSONObject(0);
                    Log.d("Log leg", leg.toString());
                    JSONObject distance = leg.getJSONObject("distance");
                    Log.d("Log distance", distance.toString());
                    JSONObject duration = leg.getJSONObject("duration");
                    Log.d("Log duration", duration.toString());
                    Log.d("Log text", duration.getString("text"));
                    timeText = duration.getString("text");
                    distanceText = distance.getString("text");
                    mTexViewTime.setText(timeText);
                    mTexViewDistance.setText(distanceText);
                } catch (Exception exception) {
                    Log.d("Error", exception.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(mOrigin));
        mMap.addMarker(new MarkerOptions().position(mDestination));

        centerMapCameraOnRoute();

        drawRoute();
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