package com.example.edriver.Interface;

import com.example.edriver.Model.DataOrder;
import com.example.edriver.Model.OrderInfo;
import com.example.edriver.Model.OrderStatus;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Андрей Кравченко on 16-Apr-17.
 */

public interface Server_API {

    @GET("/api/workers/verification/{phone}")
    Call<ResponseBody> isUserExist(@Path("phone") String phoneNumber);

    @GET("/api/code/{phone}")
    Call<ResponseBody> get_code(@Path("phone") String phoneNumber);

    @GET("/api/workers/api_key")
    Call<ResponseBody> signIn(@Header("phone") String phoneNumber, @Header("code") String code);

    @PUT("/api/workers/status/{new_status}")
    Call<ResponseBody> change_worker_status(@Header("api_key") String api_key, @Path("new_status") int new_status);

    @GET("/api/workers/orders")
    Call<DataOrder> getOrder(@Header("api_key") String api_key);

    @PUT("/api/workers/location")
    Call<ResponseBody> updateLocation(@Header("api_key") String api_key, @Body JsonObject jsonData);

    @PUT("/api/orders/{order_id}/status/{new_status}")
    Call<ResponseBody> changeOrderStatus(@Header("api_key") String api_key, @Path("order_id") int order_id, @Path("new_status") int new_status);

    @GET("/api/orders/{order_id}/status")
    Call<OrderStatus> get_order_status(@Header("api_key") String api_key, @Path("order_id") int order_id);

    @GET("/api/orders/{order_id}/info")
    Call<OrderInfo> get_order_info(@Header("api_key") String api_key, @Path("order_id") int order_id);

}
