package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.OrderData;

public interface CreateOrderCallBack {
    public void created(boolean result, OrderData worker);
}
