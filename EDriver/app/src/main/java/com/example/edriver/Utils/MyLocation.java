package com.example.edriver.Utils;

/**
 * Created by Андрей Кравченко on 17-Apr-17.
 */

public class MyLocation {
    private static MyLocation myLocation = new MyLocation();
    private MyLocation(){

    }

    public static MyLocation getInstance(){
        return myLocation;
    }

    private double latitude;
    private double longitude;
    private boolean isNew;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
