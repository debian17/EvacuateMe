package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.OrderHistory;

import java.util.List;

/**
 * Created by Андрей Кравченко on 03-May-17.
 */

public interface GetOrderHistoryCallBack {
    public void completed(boolean result, List<OrderHistory> list_orders);
}
