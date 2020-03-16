package com.smartsum.smartsumparking.pojo;

public class ParkingSpace {
    private String id;
    private String name;
    private String occupied;
    private String latitude;
    private String longitude;
    private String disabled;
    private String handicap;

    //CONSTRUCTORS

    public ParkingSpace() {}

    public ParkingSpace(String id) {
        this.id = id;
    }

    public ParkingSpace(String id, String name, String occupied, String latitude, String longitude, String disabled, String handicap) {
        this.id = id;
        this.name = name;
        this.occupied = occupied;
        this.latitude = latitude;
        this.longitude = longitude;
        this.disabled = disabled;
        this.handicap = handicap;
    }

    //GETTERS
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOccupied() {
        return occupied;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDisabled() {
        return disabled;
    }

    public String getHandicap() {
        return handicap;
    }

    //SETTERS
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOccupied(String occupied) {
        this.occupied = occupied;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }
}
