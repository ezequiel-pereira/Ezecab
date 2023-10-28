package com.ezedev.ezecab.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.activities.MainActivity;
import com.ezedev.ezecab.activities.passenger.PassengerMapActivity;
import com.ezedev.ezecab.providers.AuthProvider;

public class DriverMapActivity extends AppCompatActivity {
    Button mButtonLogout;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        mButtonLogout = findViewById(R.id.btnLogout);
        mAuthProvider = new AuthProvider();
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthProvider.logout();
                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}