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

public interface Server_API {

    @GET("/api/workers/verification/{phone}")
    Call<ResponseBody> isUserExist(@Path("phone") String phoneNumber);

    @GET("/api/code/{phone}")
    Call<ResponseBody> getCode(@Path("phone") String phoneNumber);

    @GET("/api/workers/api_key")
    Call<ResponseBody> signIn(@Header("phone") String phoneNumber, @Header("code") String code);

    @PUT("/api/workers/status/{new_status}")
    Call<ResponseBody> changeWorkerStatus(@Header("api_key") String api_key, @Path("new_status") int new_status);

    @GET("/api/workers/orders")
    Call<DataOrder> getOrder(@Header("api_key") String api_key);

    @PUT("/api/workers/location")
    Call<ResponseBody> updateLocation(@Header("api_key") String api_key, @Body JsonObject jsonData);

    @PUT("/api/orders/{order_id}/status/{new_status}")
    Call<ResponseBody> changeOrderStatus(@Header("api_key") String api_key, @Path("order_id") int order_id,
                                         @Path("new_status") int new_status);

    @GET("/api/orders/{order_id}/status")
    Call<OrderStatus> getOrderStatus(@Header("api_key") String api_key, @Path("order_id") int order_id);

    @GET("/api/orders/{order_id}/info")
    Call<OrderInfo> getOrderInfo(@Header("api_key") String api_key, @Path("order_id") int order_id);
}
