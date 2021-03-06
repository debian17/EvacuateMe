package com.example.evacuateme.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderData {
    @SerializedName("order_id")
    @Expose
    public Integer order_id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("latitude")
    @Expose
    public Double latitude;

    @SerializedName("longitude")
    @Expose
    public Double longitude;

    @SerializedName("phone")
    @Expose
    public String phone;
}
