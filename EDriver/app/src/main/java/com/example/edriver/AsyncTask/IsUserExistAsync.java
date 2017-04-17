package com.example.edriver.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.edriver.Interface.IsUserExistCallBack;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.STATUS;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 16-Apr-17.
 */

public class IsUserExistAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {

    private Context context;
    private String phoneNumber;
    private IsUserExistCallBack isUserExistCallBack;
    private ProgressDialog progressDialog;

    public IsUserExistAsync(Context context, String phoneNumber, IsUserExistCallBack isUserExistCallBack){
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.isUserExistCallBack = isUserExistCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Определяю Вас в системе...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        try {
            return App.getApi().isUserExist(phoneNumber).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<ResponseBody> responseBody) {
        super.onPostExecute(responseBody);
        progressDialog.dismiss();
        boolean isExist = false;

        if(responseBody == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        switch (responseBody.code()){
            case STATUS.Ok:{
                isExist = true;
                break;
            }
            case STATUS.BadRequest:{
                Toast.makeText(context, "Неверно введен номер телефона!", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
            case STATUS.NotFound:{
                isExist = false;
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера!", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
        }
        isUserExistCallBack.completed(isExist);
    }
}
