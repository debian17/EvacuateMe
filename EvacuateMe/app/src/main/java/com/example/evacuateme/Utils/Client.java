package com.example.evacuateme.Utils;

/**
 * Created by Андрей Кравченко on 21-Apr-17.
 */

public class Client {
    private static Client client = new Client();
    private Client(){
    }

    public static Client getInstance(){
        return client;
    }

    private Double latitude;
    private Double longitude;
    private String comment;
    private int car_type;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCar_type() {
        return car_type;
    }

    public void setCar_type(int car_type) {
        this.car_type = car_type;
    }

}
