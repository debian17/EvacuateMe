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

import com.example.edriver.Model.DataOrder;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.MyLocation;
import com.example.edriver.Utils.STATUS;
import com.google.gson.JsonObject;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 17-Apr-17.
 */

public class UpdateLocationService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private String api_key;
    private MyLocation myLocation = MyLocation.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d("UPDATE_LOCATION", "ЗАПУСТИЛСЯ");
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
        //Log.d("UPDATE_LOCATION", "ОСТАНОВИЛСЯ");
    }

    private void Run(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if(!myLocation.isNew()){
                    //Log.d("LOCATION", "НЕ ОТПРАВЛЯЮ КООРДИНАТЫ");
                    return;
                }
                //Log.d("LOCATION", "ОТПРАВЛЯЮ КООРДИНАТЫ");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("latitude", myLocation.getLatitude());
                jsonObject.addProperty("longitude", myLocation.getLongitude());
                try {
                    App.getApi().updateLocation(api_key, jsonObject).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response == null){
                            }
                            if(response.code() == STATUS.Ok){
                                myLocation.setNew(false);
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
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
