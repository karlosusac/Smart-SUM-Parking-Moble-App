package com.smartsum.smartsumparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartsum.smartsumparking.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    //viewBinding
    private ActivityMainBinding binding;

    //Firebase
        //Firebase auth
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //Firebase database
        private FirebaseDatabase db = FirebaseDatabase.getInstance();
        //Firebase Reference
        private DatabaseReference parking1 = db.getReference("0");


    //Parking variables
    private String parkingLat;
    private String parkingLng;

    //Buttons
    private Button openMaps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        openMaps = binding.button;

        openMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
            }
        });



        getParkingLatLng();
    }

    private void getParkingLatLng(){
        parking1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkingLat = dataSnapshot.child("lat").getValue().toString();
                parkingLng = dataSnapshot.child("lng").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
