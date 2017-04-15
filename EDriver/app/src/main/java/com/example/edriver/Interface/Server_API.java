package com.example.edriver.Interface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by Андрей Кравченко on 16-Apr-17.
 */

public interface Server_API {

    @GET("/api/workers/verification/{phone}")
    Call<ResponseBody> get_code(@Path("phone") String phoneNumber);

    @GET("/api/workers/api_key")
    Call<ResponseBody> signIn(@Header("phone") String phoneNumber, @Header("code") String code);
}
