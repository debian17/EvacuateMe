package com.example.evacuateme.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Companies {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("tariff")
    @Expose
    public Double tariff;

    @SerializedName("min_sum")
    @Expose
    public Double minSum;

    @SerializedName("logo_url")
    @Expose
    public String logoUrl;

    @SerializedName("rate")
    @Expose
    public Double rate;

    @SerializedName("closest_duration")
    @Expose
    public String closestDuration;

    @SerializedName("closest_distance")
    @Expose
    public Double closestDistance;

    @SerializedName("closest_worker_id")
    @Expose
    public Integer closestWorkerId;

    @Override
    public String toString() {
        return name;
    }
}
