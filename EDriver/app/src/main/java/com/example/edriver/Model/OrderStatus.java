package com.example.edriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderStatus {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("description")
    @Expose
    public String description;
}
