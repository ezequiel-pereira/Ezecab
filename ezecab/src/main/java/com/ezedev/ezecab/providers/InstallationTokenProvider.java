package com.ezedev.ezecab.providers;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ezedev.ezecab.model.InstallationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class InstallationTokenProvider {

    DatabaseReference mDatabase;

    public InstallationTokenProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("installation_token");
    }

    public void create(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        InstallationToken newToken = new InstallationToken(token);
                        mDatabase.child(userId).setValue(newToken);
                    }
                });
    }
}
