package com.example.evacuateme.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Андрей Кравченко on 30-Apr-17.
 */

public class WorkerLocation {

    @SerializedName("latitude")
    @Expose
    public Double latitude;

    @SerializedName("longitude")
    @Expose
    public Double longitude;
}
