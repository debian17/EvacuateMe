package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.OrderData;

/**
 * Created by Андрей Кравченко on 21-Apr-17.
 */

public interface CreateOrderCallBack {
    public void created(boolean result, OrderData worker);
}
