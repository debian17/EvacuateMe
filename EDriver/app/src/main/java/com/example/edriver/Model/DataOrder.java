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

    @SerializedName("commentary")
    @Expose
    public String comment;

    @SerializedName("order_id")
    @Expose
    public int order_id;
}
