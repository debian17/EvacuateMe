package com.example.edriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Андрей Кравченко on 23-Apr-17.
 */

public class OrderStatus {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("description")
    @Expose
    public String description;
}
