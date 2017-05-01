package com.example.edriver.Utils;

public class Order {
    private static Order order = new Order();
    private Order(){
        this.order_status= -1;
    }
    public static Order getInstance(){
        return order;
    }

    private Double latitude;
    private Double longitude;
    private String phone;
    private int order_id;
    private String comment;
    private int order_status;

    public static final int Awaiting = 0;
    public static final int OnTheWay = 1;
    public static final int Performing = 2;
    public static final int Completed = 3;
    public static final int CanceledByWorker = 4;
    public static final int CanceledByClient = 5;

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
