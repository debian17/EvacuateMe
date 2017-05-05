package com.example.evacuateme.Service;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.evacuateme.Activity.NavigationDrawerActivity;
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
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key","");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        worker = Worker.getInstance();
        order_id = worker.getOrder_id();
        Log.d("COSS", "CREATE "+"OID= "+String.valueOf(order_id));
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
        Log.d("COSS", "DESTROY");
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
                            Log.d("COSS", "WORK = " + String.valueOf(response.code()));
                            switch (response.code()){
                                case STATUS.Ok:{
                                    Log.d("COSS", "WORK_STATUS "+String.valueOf(response.body().description));
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
                                                Log.d("ACTIVITY", String.valueOf(NavigationDrawerActivity.active));
                                                if(NavigationDrawerActivity.active){
                                                    Intent intent = new Intent(MyAction.OrderPerforming);
                                                    LocalBroadcastManager.getInstance(CheckOrderStatusService.this).sendBroadcast(intent);
                                                }
                                                else {
                                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(CheckOrderStatusService.this)
                                                            .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                                                            .setContentTitle("Водитель прибыл!")
                                                            .setContentText("Нажмите для просмотра");
                                                    Intent intent = new Intent(CheckOrderStatusService.this, NavigationDrawerActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(CheckOrderStatusService.this, 0, intent,
                                                            PendingIntent.FLAG_CANCEL_CURRENT);
                                                    mBuilder.setContentIntent(pendingIntent);
                                                    mBuilder.mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                                                    notificationManager.notify(0, mBuilder.build());
                                                }
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
                                    Intent service_intent = new Intent(CheckOrderStatusService.this, GetWorkerLocationService.class);
                                    stopService(service_intent);
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
