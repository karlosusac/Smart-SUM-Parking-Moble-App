package com.smartsum.smartsumparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartsum.smartsumparking.databinding.ActivitySplashScreenBinding;
import com.smartsum.smartsumparking.pojo.Parking;
import com.smartsum.smartsumparking.pojo.ParkingSpace;

public class SplashScreenActivity extends AppCompatActivity {

    //View binding
    private ActivitySplashScreenBinding binding;

    //Firebase
        //Firebase auth
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Firebase database
        private FirebaseDatabase db = FirebaseDatabase.getInstance();

        //Firebase Reference
        private DatabaseReference parking1Ref = db.getReference("0");
        private DatabaseReference freeSpacesRef = parking1Ref.child("normal_available");

    //Class variables
    private LatLng parkingPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();

        //Loading parking data
        getParkingSpacesInfo();

    }

    //Initialize most of the Parking variables and get the parking spaces data from the Firebase
    private void getParkingSpacesInfo(){
        parking1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Parking.id = Integer.valueOf(dataSnapshot.child("id").getValue().toString());
                Parking.name = dataSnapshot.child("name").getValue().toString();
                Parking.address = dataSnapshot.child("address").getValue().toString();
                Parking.overallSpaces = Integer.valueOf(dataSnapshot.child("capacity").getValue().toString());
                Parking.latitude = Double.valueOf(dataSnapshot.child("lat").getValue().toString());
                Parking.longitude = Double.valueOf(dataSnapshot.child("lng").getValue().toString());

                for(DataSnapshot obj : dataSnapshot.child("parkingSpaces").getChildren()){

                    ParkingSpace parkingSpace = new ParkingSpace(String.valueOf(obj.child("id").getValue()),
                            String.valueOf(obj.child("parking_space_name").getValue()),
                            String.valueOf(obj.child("occupied").getValue()),
                            String.valueOf(obj.child("lat").getValue()),
                            String.valueOf(obj.child("lng").getValue()),
                            String.valueOf(obj.child("disabled").getValue()),
                            String.valueOf(obj.child("handicap").getValue())
                    );

                    Parking.parkingSpaces.add(parkingSpace);


                    //Get the number of the available parking spaces
                    if(!parkingSpace.getOccupied().equals("1") && !parkingSpace.getDisabled().equals("1")){
                        Parking.availableSpaces += 1;
                    }

                }

                //After getting all data, move to the Main Activity
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
}
