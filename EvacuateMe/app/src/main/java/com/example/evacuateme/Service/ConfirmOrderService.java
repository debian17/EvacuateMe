package com.example.evacuateme.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.evacuateme.Model.OrderStatus;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.Client;
import com.example.evacuateme.Utils.STATUS;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 21-Apr-17.
 */

public class ConfirmOrderService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private String api_key;
    private int order_id;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key","");
        Client client = Client.getInstance();
        order_id = client.getOrder_id();
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
        timer.schedule(timerTask, 0, 3000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void Run(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    App.getApi().get_order_status(api_key, order_id).enqueue(new Callback<OrderStatus>() {
                        @Override
                        public void onResponse(Call<OrderStatus> call, Response<OrderStatus> response) {
                            if(response == null){
                                Log.d("LOCATION", "ОТВЕТ НУЛЛ");
                                return;
                            }
                            switch (response.code()){
                                case STATUS.Ok:{
                                    Log.d("CONFIRM_SERVICE", "STATUS 200");
                                    switch (response.body().id){
                                        case STATUS.Awaiting:{
                                            Log.d("ORDER_STATUS", "ЗАКАЗ ОЖИДАЕТ ПОДТВЕРЖДЕНИЯ!");
                                            stopSelf();
                                            break;
                                        }
                                        case STATUS.OnTheWay:{
                                            Log.d("ORDER_STATUS", "ЗАКАЗ БЫЛ ПРИНЯТ!");
                                            stopSelf();
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
}
