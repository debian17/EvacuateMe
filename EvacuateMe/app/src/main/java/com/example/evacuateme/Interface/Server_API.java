package com.example.evacuateme.Interface;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Андрей Кравченко on 14-Apr-17.
 */

public interface Server_API {

    @GET("/api/clients/verification/{phone}")
    Call<ResponseBody> isUserExists(@Path("phone") String phoneNumber);

    @GET("/api/clients/code/{phone}")
    Call<ResponseBody> get_code(@Path("phone") String phoneNumber);

    @POST("/api/clients")
    Call<String> signUp(@Body JsonObject jsonData);
}
