package com.example.evacuateme.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.example.evacuateme.Model.WorkerLocation;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.MyAction;
import com.example.evacuateme.Utils.STATUS;
import com.example.evacuateme.Utils.Worker;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetWorkerLocationService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private String api_key;
    private Worker worker;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key","");
        worker = Worker.getInstance();
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
        timer.schedule(timerTask, 0, 5000);
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
                    App.getApi().getWorkerLocation(api_key, worker.getWorker_id()).enqueue(new Callback<WorkerLocation>() {
                        @Override
                        public void onResponse(Call<WorkerLocation> call, Response<WorkerLocation> response) {
                            if(response == null){
                                return;
                            }
                            switch (response.code()){
                                case STATUS.Ok:{
                                    worker.setLatitude(response.body().latitude);
                                    worker.setLongitude(response.body().longitude);
                                    switch (worker.getOrder_status()){
                                        case STATUS.OnTheWay:{
                                            Intent intent = new Intent(MyAction.WorkerLocationChanged);
                                            LocalBroadcastManager.getInstance(GetWorkerLocationService.this).sendBroadcast(intent);
                                            break;
                                        }
                                        case STATUS.Performing:{
                                            Intent intent = new Intent(MyAction.OrderLocationChanged);
                                            LocalBroadcastManager.getInstance(GetWorkerLocationService.this).sendBroadcast(intent);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case STATUS.NotFound:{
                                    break;
                                }

                                case STATUS.BadRequest:{
                                    break;
                                }

                                case STATUS.Unauthorized:{
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<WorkerLocation> call, Throwable t) {
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
