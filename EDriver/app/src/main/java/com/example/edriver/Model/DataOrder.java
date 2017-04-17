package com.example.edriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Андрей Кравченко on 17-Apr-17.
 */

public class DataOrder {
    @SerializedName("Latitude")
    @Expose
    public Double latitude;

    @SerializedName("Longitude")
    @Expose
    public Double longitude;

    @SerializedName("ClientPhone")
    @Expose
    public String clientPhone;
}
