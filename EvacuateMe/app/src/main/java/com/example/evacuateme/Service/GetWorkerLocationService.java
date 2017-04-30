package com.example.evacuateme.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.evacuateme.Model.OrderStatus;
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

/**
 * Created by Андрей Кравченко on 29-Apr-17.
 */

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
        Log.d("Я", "СОЗДАЛСО БЛЕАТЬ!");
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
        Log.d("Я", "ОСТАНОВИЛСО БЛЕАТЬ!");
    }

    private void Run(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    App.getApi().get_worker_location(api_key, worker.getWorker_id()).enqueue(new Callback<WorkerLocation>() {
                        @Override
                        public void onResponse(Call<WorkerLocation> call, Response<WorkerLocation> response) {
                            if(response == null){
                                Log.d("LOCATION", "ОТВЕТ НУЛЛ");
                                return;
                            }
                            switch (response.code()){
                                case STATUS.Ok:{
                                    Log.d("GWLLS", "КООРДИНАТЫ РАБОТНИКА ОБНОВЛЕНЫ!");
                                    worker.setLatitude(response.body().latitude);
                                    worker.setLongitude(response.body().longitude);
                                    Intent intent = new Intent(MyAction.WorkerLocationChanged);
                                    LocalBroadcastManager.getInstance(GetWorkerLocationService.this).sendBroadcast(intent);
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
                                default:{
                                    Log.d("GWLS", "Внутренняя ошибка сервера!");
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<WorkerLocation> call, Throwable t) {
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