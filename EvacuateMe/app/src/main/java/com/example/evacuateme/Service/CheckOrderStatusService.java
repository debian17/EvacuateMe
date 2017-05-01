package com.example.evacuateme.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.example.evacuateme.Model.OrderStatus;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.MyAction;
import com.example.evacuateme.Utils.STATUS;
import com.example.evacuateme.Utils.Worker;

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
    private int order_id;
    private Worker worker;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key","");
        worker = Worker.getInstance();
        order_id = worker.getOrder_id();
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
                    App.getApi().getOrderStatus(api_key, order_id).enqueue(new Callback<OrderStatus>() {
                        @Override
                        public void onResponse(Call<OrderStatus> call, Response<OrderStatus> response) {
                            if(response == null){
                                return;
                            }
                            switch (response.code()){
                                case STATUS.Ok:{
                                    switch (response.body().id){
                                        case STATUS.Awaiting:{
                                            break;
                                        }
                                        case STATUS.CanceledByWorker:{
                                            worker.setOrder_status(STATUS.CanceledByWorker);
                                            Intent intent = new Intent(MyAction.OrderCanceledByWorker);
                                            LocalBroadcastManager.getInstance(CheckOrderStatusService.this).sendBroadcast(intent);
                                            stopSelf();
                                            break;
                                        }
                                        case STATUS.OnTheWay:{
                                            if(worker.getOrder_status()!=STATUS.OnTheWay){
                                                worker.setOrder_status(STATUS.OnTheWay);
                                                Intent intent = new Intent(MyAction.OrderConfirmed);
                                                LocalBroadcastManager.getInstance(CheckOrderStatusService.this).sendBroadcast(intent);
                                            }
                                            break;
                                        }
                                        case STATUS.Performing:{
                                            if(worker.getOrder_status()!=STATUS.Performing){
                                                worker.setOrder_status(STATUS.Performing);
                                                Intent intent = new Intent(MyAction.OrderPerforming);
                                                LocalBroadcastManager.getInstance(CheckOrderStatusService.this).sendBroadcast(intent);
                                            }
                                            break;
                                        }
                                        case STATUS.Completed:{
                                            worker.setOrder_status(STATUS.Completed);
                                            Intent intent = new Intent(MyAction.OrderCompleted);
                                            LocalBroadcastManager.getInstance(CheckOrderStatusService.this).sendBroadcast(intent);
                                            break;
                                        }
                                    }
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
}
