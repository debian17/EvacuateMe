package com.example.evacuateme.Interface;

import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.Model.Companies;
import com.example.evacuateme.Model.OrderStatus;
import com.example.evacuateme.Model.Worker;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Андрей Кравченко on 14-Apr-17.
 */

public interface Server_API {

    @GET("/api/clients/verification/{phone}")
    Call<ResponseBody> isUserExists(@Path("phone") String phoneNumber);

    @GET("/api/code/{phone}")
    Call<ResponseBody> get_code(@Path("phone") String phoneNumber);

    @POST("/api/clients")
    Call<ResponseBody> signUp(@Body JsonObject jsonData);

    @GET("/api/clients/api_key")
    Call<ResponseBody> signIn(@Header("phone") String phoneNumber, @Header("code") String code);

    @GET("/api/car_types")
    Call<List<CarType>> get_car_type(@Header("api_key") String api_key);

    @POST("api/help/companies")
    Call<List<Companies>> get_companies(@Header("api_key") String api_key, @Body JsonObject jsonObject);

    @POST("/api/orders")
    Call<Worker> create_order(@Header("api_key") String api_key, @Body JsonObject jsonObject);

    @GET("/api/orders/{order_id}/status")
    Call<OrderStatus> get_order_status(@Header("api_key") String api_key, @Path("order_id") int order_id);
}
