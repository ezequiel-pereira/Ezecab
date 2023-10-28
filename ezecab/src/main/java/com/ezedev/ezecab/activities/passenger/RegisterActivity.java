package com.ezedev.ezecab.activities.passenger;

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
import com.ezedev.ezecab.activities.driver.RegisterDriverActivity;
import com.ezedev.ezecab.includes.MyToolbar;
import com.ezedev.ezecab.model.Passenger;
import com.ezedev.ezecab.providers.AuthProvider;
import com.ezedev.ezecab.providers.PassengerProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText mTextInputName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    PassengerProvider mPassengerProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTextInputName = findViewById(R.id.textImputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonRegister = findViewById(R.id.btnRegister);

        MyToolbar.show(this, "Regsitro de usuario", true);

        mAuthProvider = new AuthProvider();
        mPassengerProvider = new PassengerProvider();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = mTextInputName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                register(name, email, password);
            } else {
                Toast.makeText(this, "La contraseña debe tener al menos seis caracteres", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debe ingresar un correo electrónico y una contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(String name, String email, String password) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Passenger passenger = new Passenger(id, name, email);
                    create(passenger);
                } else {
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void create(Passenger passenger) {
        mPassengerProvider.create(passenger).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, PassengerMapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "No se pudo registar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    private void saveUser(String id, String name, String email) {
        String  userType = mPref.getString("user", "");

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        if (userType.equals("driver")) {
            mDatabase.child("users").child("drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (userType.equals("client")) {
            mDatabase.child("users").child("clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }*/
}