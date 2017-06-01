package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.evacuateme.Interface.ChangeSettingsCallBack;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.STATUS;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ChangeSettingsAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {
    private Context context;
    private String api_key;
    private String car_model;
    private String car_colour;
    private ChangeSettingsCallBack changeSettingsCallBack;
    private ProgressDialog progressDialog;

    public ChangeSettingsAsync(Context context, String api_key, String car_model, String car_colour, ChangeSettingsCallBack changeSettingsCallBack){
        this.context = context;
        this.api_key = api_key;
        this.car_model = car_model;
        this.car_colour = car_colour;
        this.changeSettingsCallBack = changeSettingsCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Сохраняю настройки..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("car_model", car_model);
        jsonObject.addProperty("car_colour", car_colour);
        try {
            return App.getApi().changeCarSettings(api_key, jsonObject).execute();
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

        if(responseBody == null){
            Toast.makeText(context, "Сервер временно не доступен!", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (responseBody.code()){
            case STATUS.Ok:{
                result = true;
                break;
            }
            case STATUS.NotFound:{
                break;
            }
            default:{
                break;
            }
        }
        changeSettingsCallBack.completed(result);
    }
}
