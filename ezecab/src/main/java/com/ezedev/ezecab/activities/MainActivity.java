package com.ezedev.ezecab.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.activities.driver.DriverMapActivity;
import com.ezedev.ezecab.activities.passenger.PassengerMapActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button mButtonIAmDriver;
    Button getmButtonIAmPassenger;

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getApplicationContext().getSharedPreferences("userType", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();

        mButtonIAmDriver = findViewById(R.id.btnIAmDriver);
        getmButtonIAmPassenger = findViewById(R.id.btnIAmPassenger);

        mButtonIAmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user", "driver");
                editor.apply();
                goToLoginregister();
            }
        });

        getmButtonIAmPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user", "client");
                editor.apply();
                goToLoginregister();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userType = mPref.getString("user", "");

            if (userType.equals("driver")) {
                Intent intent = new Intent(MainActivity.this, DriverMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, PassengerMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void goToLoginregister() {
        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
        startActivity(intent);
    }
}