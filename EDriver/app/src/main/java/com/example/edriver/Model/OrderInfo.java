package com.example.edriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderInfo {

    @SerializedName("order_id")
    @Expose
    public Integer order_id;

    @SerializedName("distance")
    @Expose
    public Double distance;

    @SerializedName("summary")
    @Expose
    public Double summary;

    @SerializedName("company")
    @Expose
    public String company;

}
