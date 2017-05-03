package com.example.edriver.Interface;

import com.example.edriver.Model.OrderHistory;

import java.util.List;

public interface GetOrderHistoryCallBack {
    public void completed(boolean result, List<OrderHistory> list_orders);
}
