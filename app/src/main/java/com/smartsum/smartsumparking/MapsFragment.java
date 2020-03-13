package com.smartsum.smartsumparking;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback {

    //Google map
    private GoogleMap mMap;

    //Firebase
        //Firebase auth
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Firebase database
        private FirebaseDatabase db = FirebaseDatabase.getInstance();

        //Firebase Reference
        private DatabaseReference parking1 = db.getReference("0");


    //Parking variables
    private double parkingLat;
    private double parkingLng;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setUpMapIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap=googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getContext(), R.raw.custom_maps));

        parking1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkingLat = (double) dataSnapshot.child("lat").getValue();
                parkingLng = (double) dataSnapshot.child("lng").getValue();

                final LatLng parking = new LatLng(parkingLat, parkingLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parking, 17));
                mMap.addMarker(new MarkerOptions().position(parking).title(dataSnapshot.child("name").getValue().toString()));

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parking, 18));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parking, 18.5f), 500, null);
                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
