package com.ezedev.ezecab.activities.passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.activities.MainActivity;
import com.ezedev.ezecab.providers.AuthProvider;

public class PassengerMapActivity extends AppCompatActivity {
    Button mButtonLogout;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);

        mButtonLogout = findViewById(R.id.btnLogout);
        mAuthProvider = new AuthProvider();
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthProvider.logout();
                Intent intent = new Intent(PassengerMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}