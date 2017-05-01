package com.example.evacuateme.Utils;

import android.app.Application;

import com.example.evacuateme.Interface.Server_API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static Server_API api;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder().baseUrl("https://evacuateme.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Server_API.class);
    }

    public static Server_API getApi() {
        return api;
    }
}
