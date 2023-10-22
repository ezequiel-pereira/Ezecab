package com.ezedev.ezecab.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.activities.driver.RegisterDriverActivity;
import com.ezedev.ezecab.activities.passenger.RegisterActivity;

public class LoginRegisterActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Button mButtonGoToLogin;
    Button mButtonGoToRegister;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Seleccionar una opción");
        //No title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonGoToLogin = findViewById(R.id.btnGoToLogin);
        mButtonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });

        mButtonGoToRegister = findViewById(R.id.btnGoToRegister);
        mButtonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        mPref = getApplicationContext().getSharedPreferences("userType", MODE_PRIVATE);
    }

    private void goToLogin() {
        Intent intent = new Intent(LoginRegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToRegister() {
        String userType = mPref.getString("userType", "");

        if (userType.equals("client")) {
            Intent intent = new Intent(LoginRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else /*if (userType.equals("driver"))*/ {
            Intent intent = new Intent(LoginRegisterActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }
    }
}