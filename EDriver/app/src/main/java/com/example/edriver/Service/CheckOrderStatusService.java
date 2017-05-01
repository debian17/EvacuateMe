package com.example.edriver.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.example.edriver.Model.OrderStatus;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.MyAction;
import com.example.edriver.Utils.Order;
import com.example.edriver.Utils.STATUS;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOrderStatusService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private String api_key;
    private Order order;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", "");
        order = Order.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timerTask!=null){
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Run();
            }
        };
        timer.schedule(timerTask, 0, 4000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        timer.cancel();
    }

    private void Run(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    App.getApi().getOrderStatus(api_key, order.getOrder_id()).enqueue(new Callback<OrderStatus>() {
                        @Override
                        public void onResponse(Call<OrderStatus> call, Response<OrderStatus> response) {
                            if(response == null){
                                return;
                            }
                            switch (response.code()){
                                case STATUS.Ok:{
                                    switch (response.body().id){
                                        case Order.CanceledByClient:{
                                            order.setOrder_status(Order.CanceledByClient);
                                            Intent intent = new Intent(MyAction.OrderCanceledByClient);
                                            intent.putExtra("canceled", true);
                                            LocalBroadcastManager.getInstance(CheckOrderStatusService.this).sendBroadcast(intent);
                                            stopSelf();
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case STATUS.NotFound:{
                                    stopSelf();
                                    break;
                                }
                                default:{
                                    stopSelf();
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<OrderStatus> call, Throwable t) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
