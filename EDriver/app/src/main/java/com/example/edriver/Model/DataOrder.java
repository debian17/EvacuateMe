package com.example.edriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataOrder {
    @SerializedName("latitude")
    @Expose
    public Double latitude;

    @SerializedName("longitude")
    @Expose
    public Double longitude;

    @SerializedName("phone")
    @Expose
    public String clientPhone;

    @SerializedName("car_model")
    @Expose
    public String car_model;

    @SerializedName("distance")
    @Expose
    public Double distance;

    @SerializedName("car_colour")
    @Expose
    public String car_colour;

    @SerializedName("order_id")
    @Expose
    public int order_id;
}
