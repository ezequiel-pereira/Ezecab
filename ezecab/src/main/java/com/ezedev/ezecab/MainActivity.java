package com.ezedev.ezecab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mButtonIAmDriver;
    Button getmButtonIAmPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonIAmDriver = findViewById(R.id.btnIAmDriver);
        getmButtonIAmPassenger = findViewById(R.id.btnIAmPassenger);

        mButtonIAmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginregister();
            }
        });

        getmButtonIAmPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginregister();
            }
        });
    }

    private void goToLoginregister() {
        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
        startActivity(intent);
    }
}