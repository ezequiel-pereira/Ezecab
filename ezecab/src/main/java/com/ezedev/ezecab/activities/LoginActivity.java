package com.ezedev.ezecab.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.activities.driver.DriverMapActivity;
import com.ezedev.ezecab.activities.passenger.PassengerMapActivity;
import com.ezedev.ezecab.includes.MyToolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;

    private FirebaseAuth mAuth;

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);

        MyToolbar.show(this, "Login de usuario", true);

        mAuth = FirebaseAuth.getInstance();
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mPref = getApplicationContext().getSharedPreferences("userType", MODE_PRIVATE);
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userType = mPref.getString("user", "");

                            if (userType.equals("driver")) {
                                Intent intent = new Intent(LoginActivity.this, DriverMapActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, PassengerMapActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "La contraseña debe tener al menos seis caracteres", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debe ingresar un correo electrónico y una contraseña", Toast.LENGTH_SHORT).show();
        }
    }
}