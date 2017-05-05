package com.example.edriver.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.edriver.Activity.NavigationDrawerActivity;
import com.example.edriver.Model.DataOrder;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.MyAction;
import com.example.edriver.Utils.Order;
import com.example.edriver.Utils.STATUS;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetOrderService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private String api_key;
    private NotificationManager notificationManager;
    private Order order;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", "");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
                                    order.setOrder_id(response.body().order_id);
                                    order.setLatitude(response.body().latitude);
                                    order.setLongitude(response.body().longitude);
                                    order.setPhone(response.body().clientPhone);
                                    order.setCar_model(response.body().car_model);
                                    order.setCar_colour(response.body().car_colour);
                                    order.setDistance(response.body().distance);
                                    order.setOrder_status(Order.Awaiting);
                                    if(NavigationDrawerActivity.active){
                                        Intent intent = new Intent(MyAction.Order);
                                        LocalBroadcastManager.getInstance(GetOrderService.this).sendBroadcast(intent);
                                    }
                                    else {
                                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GetOrderService.this)
                                                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                                                .setContentTitle("У Вас новый заказ!")
                                                .setContentText("Нажмите для просмотра");
                                        Intent intent = new Intent(GetOrderService.this, NavigationDrawerActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(GetOrderService.this, 0, intent,
                                                PendingIntent.FLAG_CANCEL_CURRENT);
                                        mBuilder.setContentIntent(pendingIntent);
                                        mBuilder.mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                                        notificationManager.notify(0, mBuilder.build());
                                    }
                                    stopSelf();
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<DataOrder> call, Throwable t) {
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
