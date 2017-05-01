package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.CarType;

import java.util.List;

public interface GetCarTypeCallBack {
    public void completed(boolean result, List<CarType> data);
}
