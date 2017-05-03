package com.example.evacuateme.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderHistory {

    @SerializedName("order_id")
    @Expose
    public int order_id;

    @SerializedName("company")
    @Expose
    public String company;

    @SerializedName("contact_phone")
    @Expose
    public String contact_phone;

    @SerializedName("beginning_time")
    @Expose
    public String beginning_time;

    @SerializedName("termination_time")
    @Expose
    public String termination_time;

    @SerializedName("duration")
    @Expose
    public String duration;

    @SerializedName("distance")
    @Expose
    public Double distance;

    @SerializedName("summary")
    @Expose
    public Double summary;

    @SerializedName("car_type")
    @Expose
    public String car_type;

    @SerializedName("rate")
    @Expose
    public Double rate;

}
