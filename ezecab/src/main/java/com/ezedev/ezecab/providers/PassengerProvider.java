package com.ezedev.ezecab.providers;

import com.ezedev.ezecab.model.Passenger;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PassengerProvider {
    DatabaseReference mDatabase;

    public PassengerProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("passengers");
    }

    public Task<Void> create(Passenger passenger) {
        return mDatabase.child(passenger.getId()).setValue(passenger);
    }
}
