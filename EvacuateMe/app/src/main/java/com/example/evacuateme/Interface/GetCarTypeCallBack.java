package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.CarType;

import java.util.List;

/**
 * Created by Андрей Кравченко on 18-Apr-17.
 */

public interface GetCarTypeCallBack {
    public void completed(boolean result, List<CarType> data);
}
