package com.example.evacuateme.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Андрей Кравченко on 01-May-17.
 */

public class OrderInfo {

    @SerializedName("order_id")
    @Expose
    public int order_id;

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
