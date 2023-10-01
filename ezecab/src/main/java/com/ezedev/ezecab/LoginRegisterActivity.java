package com.ezedev.ezecab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.ezedev.ezecab.R;

public class LoginRegisterActivity extends AppCompatActivity {

    Toolbar mToolbar;
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
    }
}