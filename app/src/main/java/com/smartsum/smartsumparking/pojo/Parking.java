package com.smartsum.smartsumparking.pojo;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class Parking {

    public static int id;
    public static String name;
    public static String address;
    public static int occupiedSpaces;
    public static int availableSpaces = 0;
    public static int overallSpaces;
    public static double latitude;
    public static double longitude;

    public static ArrayList<ParkingSpace> parkingSpaces = new ArrayList<ParkingSpace>();
    public static ArrayList<Marker> parkingSpaceMarkers = new ArrayList<Marker>();
    public static ArrayList<Marker> parkingMarkers = new ArrayList<Marker>();

    //CONSTRUCTORS

    //Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getOccupiedSpaces() {
        return occupiedSpaces;
    }

    public int getAvailableSpaces() {
        return availableSpaces;
    }

    public int getOverallSpaces() {
        return overallSpaces;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOccupiedSpaces(int occupiedSpaces) {
        this.occupiedSpaces = occupiedSpaces;
    }

    public void setAvailableSpaces(int availableSpaces) {
        this.availableSpaces = availableSpaces;
    }

    public void setOverallSpaces(int overallSpaces) {
        this.overallSpaces = overallSpaces;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
