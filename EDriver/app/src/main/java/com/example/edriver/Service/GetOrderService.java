package com.example.edriver.Service;

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

import com.example.edriver.Activity.NavigationDrawerActivity;
import com.example.edriver.Model.DataOrder;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.STATUS;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 17-Apr-17.
 */

public class GetOrderService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private String api_key;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", "");
        Log.d("TAG", "КОНСТРУКТОР СЕРВИСА");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "ON_START_COMMAND");
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
        Log.d("TAG", "TIMER начал работу");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        timer.cancel();
        Log.d("TAG", "DESTROY");
    }

    private void Run(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    App.getApi().getOrder(api_key).enqueue(new Callback<DataOrder>() {
                        @Override
                        public void onResponse(Call<DataOrder> call, Response<DataOrder> response) {
                            if(response == null){
                                return;
                            }
                            switch (response.code()){
                                case STATUS.NotFound:{
                                    break;
                                }
                                case STATUS.Ok:{
                                    Log.d("SERVICE", "ЕСТЬ ЗАКАЗ");
                                    timerTask.cancel();
                                    timer.cancel();
//                                    Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
//                                    intent.putExtra("latitude", response.body().latitude);
//                                    intent.putExtra("longitude", response.body().longitude);
//                                    intent.putExtra("clientPhone", response.body().clientPhone);
//                                    intent.putExtra("isOrder", true);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
                                    break;
                                }
                                default:{
                                    Log.d("TAG", "Внетренняя ошибка сервера!");
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<DataOrder> call, Throwable t) {
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
