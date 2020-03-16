package com.smartsum.smartsumparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartsum.smartsumparking.databinding.ActivityMapsBinding;
import com.smartsum.smartsumparking.pojo.Parking;
import com.smartsum.smartsumparking.pojo.ParkingSpace;

public class MapsFragment extends Fragment {

    public MapView mMapView;
    private GoogleMap mMap;

    //Databinding
    private ActivityMapsBinding binding;

    //Firebase
        //Firebase auth
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Firebase database
        private FirebaseDatabase db = FirebaseDatabase.getInstance();

        //Firebase Reference
        private DatabaseReference parking1Ref = db.getReference("0");
        private DatabaseReference freeSpacesRef = parking1Ref.child("normal_available");
        private DatabaseReference parking1ParkingSpaces = parking1Ref.child("parkingSpaces");

    //Num of disabled parking spaces
    private int numOfDisabledParkingSpaces;

    //Context
    private Context mContext; //Context for the application is declared global because if it isn't, on screen rotation context will restart itself and then whole application will flip

    //Buttons
    private FloatingActionButton zoomInAndOutBtn; //Zoom in and out button
    private Button freeSpacesShow; //Button that shows number of free spaces

    //zoom level
    private float zoomLevel;

    //Parking icon parameters
    private BitmapDrawable bitmapdraw;
    private Bitmap b;
    private Bitmap smallMarker;

    //Parking icons
    private BitmapDrawable availableParkingSpaceBmp;
    private BitmapDrawable occupiedParkingSpaceBmp;
    private BitmapDrawable disabledParkingSpaceBmp;
    private BitmapDrawable availableParkingSpaceHandicapBmp;
    private BitmapDrawable occupiedParkingSpaceHandicapBmp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout. activity_maps, null, false);

        //TODO pobrisati ovo ispod i treba biti negdje ovdije dio za inicijaliziranje slika jer inače jede govna
        //TODO sinoć sam prilikom testiranja umalo dobrio srčani kada je aplikacije crash-ala, ali u bazi se samo dodalo parking mjesto 62 koje izaziva error

        mMapView = (MapView) binding.mapView;
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        //Initialize variables
        zoomInAndOutBtn = binding.mapFragZoomBtn;
        freeSpacesShow = binding.freeParkingSpaces;

        availableParkingSpaceBmp = (BitmapDrawable)getResources().getDrawable(R.drawable.circle_green);
        occupiedParkingSpaceBmp = (BitmapDrawable)getResources().getDrawable(R.drawable.circle_red);
        disabledParkingSpaceBmp = (BitmapDrawable)getResources().getDrawable(R.drawable.circle_gray);
        availableParkingSpaceHandicapBmp = (BitmapDrawable)getResources().getDrawable(R.drawable.circle_green_handicap);
        occupiedParkingSpaceHandicapBmp = (BitmapDrawable)getResources().getDrawable(R.drawable.circle_red_handicap);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //update Parking info from the firebase
        getParkingInfo();

        //Set up initial display of available parking spaces
        freeSpacesShow.setText(String.valueOf(Parking.availableSpaces));

        //Listener for the changes of the parking spaces
        parkingSpaceStatusListener();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                mContext, R.raw.custom_maps));

                //Zoom on the Parking
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Parking.latitude, Parking.longitude), 17.0f), 500, null);

                //Make and display Parking Marker
                setUpParkingMarker();


                //Map zoom listener
                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        zoomImgAndIconsListener();
                    }
                });

                //If parking marker is clicked zoom on it, else show parking space info
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if(marker.getId().equals("m0")){
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 19.5f), 500, null);
                        } else {
                            marker.showInfoWindow();
                        }

                        return true;
                    }
                });

                //Zoom in or out btn event
                zoomInAndOutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zoomInOrOut();
                    }
                });

                //Depending on the zoom level change zoomBtn icon if needed and show/hide parking or parking spaces markers
                zoomImgAndIconsListener();
            }
        });

        //Listen for free spaces changes and update them
        //availableSpacesChangeListener();
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();

    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void zoomInOrOut(){
        if(zoomLevel < 18.5f){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19.5f), 500, null);
        } else {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 500, null);
        }
    }

    private void getParkingInfo(){
        parking1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            int index = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*
                Parking.id = Integer.valueOf(dataSnapshot.child("id").getValue().toString());
                Parking.name = dataSnapshot.child("name").getValue().toString();
                Parking.address = dataSnapshot.child("address").getValue().toString();
                Parking.overallSpaces = Integer.valueOf(dataSnapshot.child("capacity").getValue().toString());
                Parking.latitude = Double.valueOf(dataSnapshot.child("lat").getValue().toString());
                Parking.longitude = Double.valueOf(dataSnapshot.child("lng").getValue().toString());
                */

                for(DataSnapshot obj : dataSnapshot.child("parkingSpaces").getChildren()){

                    ParkingSpace parkingSpace = new ParkingSpace(String.valueOf(obj.child("id").getValue()),
                            String.valueOf(obj.child("parking_space_name").getValue()),
                            String.valueOf(obj.child("occupied").getValue()),
                            String.valueOf(obj.child("lat").getValue()),
                            String.valueOf(obj.child("lng").getValue()),
                            String.valueOf(obj.child("disabled").getValue()),
                            String.valueOf(obj.child("handicap").getValue())
                    );

                    Parking.parkingSpaces.set(index ,parkingSpace);
                    index += 1;
                    Log.d("index", String.valueOf(index));

                    //Make the new LatLng position for the marker
                    LatLng parkingPosition = new LatLng(Double.valueOf(parkingSpace.getLatitude()), Double.valueOf(parkingSpace.getLongitude()));


                    bitmapdraw = null;
                    if(parkingSpace.getDisabled().equals("1")){
                        b = disabledParkingSpaceBmp.getBitmap();
                    } else {
                        if(parkingSpace.getOccupied().equals("1")){
                            if(parkingSpace.getHandicap().equals("1")){
                                b = occupiedParkingSpaceHandicapBmp.getBitmap();
                            } else {
                                b = occupiedParkingSpaceBmp.getBitmap();
                            }
                        } else {
                            if(parkingSpace.getHandicap().equals("1")){
                                b = availableParkingSpaceHandicapBmp.getBitmap();
                            } else {
                                b = availableParkingSpaceBmp.getBitmap();
                            }
                        }
                    }

                    //Downslace an image
                    smallMarker = Bitmap.createScaledBitmap(b, 40, 40, false);
                    //Make an marker
                    Marker marker = mMap.addMarker((new MarkerOptions().position(parkingPosition).title(parkingSpace.getName())).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    //Hide the marker at the start
                    marker.setVisible(false);
                    //Add the marker in the parkingSpaceMarkers list
                    Parking.parkingSpaceMarkers.add(marker);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Listener for the parking occupied/available changes that updates the Parking.parkingSpaces list and Parking.parkingSpaceMarkers list
    private void parkingSpaceStatusListener(){
        parking1ParkingSpaces.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //Get the index of the changed child and update the value
                int index = Integer.valueOf(String.valueOf(dataSnapshot.child("id").getValue())) - 1;
                Parking.parkingSpaces.get(index).setOccupied(String.valueOf(dataSnapshot.child("occupied").getValue()));

                //Decision tree that decides what image will be displayed
                bitmapdraw = null;
                if(String.valueOf(dataSnapshot.child("disabled").getValue()).equals("1")){
                    b = disabledParkingSpaceBmp.getBitmap();
                } else {
                    if(String.valueOf(dataSnapshot.child("occupied").getValue()).equals("1")){
                        if(String.valueOf(dataSnapshot.child("handicap").getValue()).equals("1")){
                            b = occupiedParkingSpaceHandicapBmp.getBitmap();
                        } else {
                            b = occupiedParkingSpaceBmp.getBitmap();
                        }
                    } else {
                        if(String.valueOf(dataSnapshot.child("handicap").getValue()).equals("1")){
                            b = availableParkingSpaceHandicapBmp.getBitmap();
                        } else {
                            b = availableParkingSpaceBmp.getBitmap();
                        }
                    }
                }

                //Downscale na image
                smallMarker = Bitmap.createScaledBitmap(b, 40, 40, false);

                //Update the icon in the parkingSpaceMarkers list
                Parking.parkingSpaceMarkers.get(index).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));


                Parking.availableSpaces = 0;
                for(ParkingSpace ps : Parking.parkingSpaces){
                    if(!ps.getOccupied().equals("1") && !ps.getDisabled().equals("1")){
                        Parking.availableSpaces += 1;
                        System.out.println(Parking.availableSpaces);
                    }
                }

                freeSpacesShow.setText(String.valueOf(Parking.availableSpaces));
                Log.d("p", String.valueOf(Parking.availableSpaces));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /*
    //Listener for the number of available parking spaces that updates the number on top of the map
    private void availableSpacesChangeListener(){
        freeSpacesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                freeSpacesShow.setText(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }
    */


    //Marker maker for the whole Parking
    private void setUpParkingMarker(){
        Marker marker = mMap.addMarker((new MarkerOptions().position(new LatLng(Parking.latitude, Parking.longitude)).title(Parking.name)));
        marker.setTag(Parking.id);
        Parking.parkingMarkers.add(marker);
    }

    //Depending on the zoom level change zoomBtn icon if needed and show/hide parking or parking spaces markers
    private void zoomImgAndIconsListener(){
        zoomLevel = mMap.getCameraPosition().zoom;
        if(zoomLevel < 18.5f){
            zoomInAndOutBtn.setImageResource(R.drawable.ic_zoom_in);

            for(Marker marker : Parking.parkingSpaceMarkers){
                marker.setVisible(false);
            }

            for(Marker marker : Parking.parkingMarkers){
                marker.setVisible(true);
            }
        } else {
            zoomInAndOutBtn.setImageResource(R.drawable.ic_zoom_out);

            for(Marker marker : Parking.parkingSpaceMarkers){
                marker.setVisible(true);
            }

            for(Marker marker : Parking.parkingMarkers){
                marker.setVisible(false);
            }
        }
    }
}
