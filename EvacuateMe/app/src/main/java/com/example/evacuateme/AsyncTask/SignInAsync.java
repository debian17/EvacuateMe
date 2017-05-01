package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.evacuateme.Interface.SignInCallBack;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.STATUS;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class SignInAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {
    private Context context;
    private String phoneNumber;
    private String code;
    private SignInCallBack signInCallBack;
    private ProgressDialog progressDialog;

    public SignInAsync(Context context, String phoneNumber, String code, SignInCallBack signInCallBack){
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.signInCallBack = signInCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Вход в систему...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        try {
            return App.getApi().signIn(phoneNumber, code).execute();
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
            case STATUS.Ok:{
                try {
                    api_key = responseBody.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = true;
                break;
            }
            case STATUS.NotFound:{
                Toast.makeText(context, "Неверно введен проверочный код!", Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.BadRequest:{
                Toast.makeText(context, "Неверно введен номер телефона или проверочный код!", Toast.LENGTH_SHORT).show();
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        signInCallBack.completed(result, api_key);
    }
}
