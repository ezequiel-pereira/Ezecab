package com.ezedev.ezecab.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ezedev.ezecab.R;

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

    private void goToLoginregister() {
        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
        startActivity(intent);
    }
}