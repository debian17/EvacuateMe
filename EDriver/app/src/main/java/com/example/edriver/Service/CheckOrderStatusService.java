package com.example.edriver.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.edriver.Activity.NavigationDrawerActivity;
import com.example.edriver.Model.DataOrder;
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

/**
 * Created by Андрей Кравченко on 29-Apr-17.
 */

public class CheckOrderStatusService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private String api_key;
    private Order order  = Order.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", "");
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
                    App.getApi().get_order_status(api_key, order.getOrder_id()).enqueue(new Callback<OrderStatus>() {
                        @Override
                        public void onResponse(Call<OrderStatus> call, Response<OrderStatus> response) {
                            if(response == null){
                                return;
                            }
                            switch (response.code()){
                                case STATUS.Ok:{
                                    switch (response.body().id){
                                        case Order.CanceledByClient:{
                                            Log.d("ORDER_STATUS", "ЗАКАЗ ОТМЕНЕН КЛИЕНТОМ!");
                                            order.setOrder_status(Order.CanceledByClient);
                                            Intent intent = new Intent(MyAction.OrderCanceledByClient);
                                            intent.putExtra("canceled", true);
                                            LocalBroadcastManager.getInstance(CheckOrderStatusService.this).sendBroadcast(intent);
                                            stopSelf();
                                            break;
                                        }
                                        case Order.OnTheWay:{
                                            Log.d("ORDER_STATUS", "Я В ПУТИ");
                                            break;
                                        }
                                        default:{
                                            Log.d("НЕ ОК", String.valueOf(response.code()));
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case STATUS.NotFound:{
                                    Log.d("CONFIRM_SERVICE", "STATUS 404");
                                    stopSelf();
                                    break;
                                }
                                default:{
                                    Log.d("CONFIRM_SERVICE", "ВНУТРЕННЯЯ ОШИБКА СЕРВЕРА");
                                    stopSelf();
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<OrderStatus> call, Throwable t) {
                            Log.d("TAG", "ВСЕ ПЛОХО!");
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
