package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.example.evacuateme.Interface.SignUpCallBack;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.STATUS;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SignUpAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {

    private Context context;
    private String phoneNumber;
    private String name;
    private String code;
    private String car_model;
    private String car_colour;
    private SignUpCallBack signUpCallBack;
    private ProgressDialog progressDialog;

    public SignUpAsync(Context context, String phoneNumber, String name, String car_model, String car_colour, String code, SignUpCallBack signUpCallBack){
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.code = code;
        this.car_colour = car_colour;
        this.car_model = car_model;
        this.signUpCallBack = signUpCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Регистрирую в системе...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phoneNumber);
        jsonObject.addProperty("car_model", car_model);
        jsonObject.addProperty("car_colour", car_colour);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("name", name);
        try {
            return App.getApi().signUp(jsonObject).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<ResponseBody> responseBody) {
        super.onPostExecute(responseBody);
        progressDialog.dismiss();
        boolean result = false;
        String api_key = "";
        if(responseBody == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже.", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (responseBody.code()){
            case STATUS.Created:{
                try {
                    api_key = responseBody.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = true;
                break;
            }
            case STATUS.BadRequest:{
                Toast.makeText(context, "Неверный формат номер телефона!", Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.NotFound:{
                Toast.makeText(context, "Неверно введен проверочный код!", Toast.LENGTH_SHORT).show();
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        signUpCallBack.completed(result, api_key);
    }
}
