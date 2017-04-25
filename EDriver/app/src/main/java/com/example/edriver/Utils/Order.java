package com.example.edriver.Utils;

/**
 * Created by Андрей Кравченко on 24-Apr-17.
 */

public class Order {
    private static Order order = new Order();
    private Order(){}
    public static Order getInstance(){
        return order;
    }

    private Double latitude;
    private Double longitude;
    private String phone;
    private int order_id;
    private String comment;

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
