package com.example.evacuateme.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Андрей Кравченко on 17-Apr-17.
 */

public class CarType {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("name")
    @Expose
    public String name;

    @Override
    public String toString() {
        return name;
    }

    public boolean selected;

}
