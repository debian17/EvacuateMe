package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.Worker;

/**
 * Created by Андрей Кравченко on 21-Apr-17.
 */

public interface CreateOrderCallBack {
    public void created(boolean result, Worker worker);
}
