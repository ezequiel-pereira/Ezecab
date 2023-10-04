package com.ezedev.ezecab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ezedev.ezecab.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText mTextInputName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonRegister;
    SharedPreferences mPref;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTextInputName = findViewById(R.id.textImputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonRegister = findViewById(R.id.btnRegister);

        mPref = getApplicationContext().getSharedPreferences("userType", MODE_PRIVATE);
        String userType = mPref.getString("user", "");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        Toast.makeText(this, "UserType: " + userType, Toast.LENGTH_SHORT).show();
    }

    private void registerUser() {
        String name = mTextInputName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = mAuth.getCurrentUser().getUid();
                            saveUser(id, name, email);
                        } else {
                            Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
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
    }
}