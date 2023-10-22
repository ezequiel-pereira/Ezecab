package com.ezedev.ezecab.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ezedev.ezecab.R;
import com.ezedev.ezecab.activities.passenger.RegisterActivity;
import com.ezedev.ezecab.includes.MyToolbar;
import com.ezedev.ezecab.model.Driver;
import com.ezedev.ezecab.providers.AuthProvider;
import com.ezedev.ezecab.providers.DriverProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterDriverActivity extends AppCompatActivity {

    TextInputEditText mTextInputName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputCarBrand;
    TextInputEditText mTextInputCarPlate;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    DriverProvider mDriverProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        mTextInputName = findViewById(R.id.textImputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputCarBrand = findViewById(R.id.textInputCarBrand);
        mTextInputCarPlate = findViewById(R.id.textInputCarPlate);
        mButtonRegister = findViewById(R.id.btnRegister);

        MyToolbar.show(this, "Regsitro de conductor", true);

        mAuthProvider = new AuthProvider();
        mDriverProvider = new DriverProvider();

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
        String carBrand = mTextInputCarBrand.getText().toString();
        String carPlate = mTextInputCarPlate.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !carBrand.isEmpty() && !carPlate.isEmpty()) {
            if (password.length() >= 6) {
                register(name, email, password, carBrand, carPlate);
            } else {
                Toast.makeText(this, "La contraseña debe tener al menos seis caracteres", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debe ingresar un correo electrónico y una contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(String name, String email, String password, String carBrand, String carPlate) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver = new Driver(id, name, email, carBrand, carPlate);
                    create(driver);
                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void create(Driver driver ) {
        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo registar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}