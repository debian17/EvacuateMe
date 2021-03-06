package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.evacuateme.Interface.GetCarTypeCallBack;
import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class GetCarTypeAsync extends AsyncTask<Void, Void, Response<List<CarType>>> {
    private Context context;
    private String api_key;
    private ProgressDialog progressDialog;
    private GetCarTypeCallBack getCarTypeCallBack;

    public GetCarTypeAsync(Context context, String api_key, GetCarTypeCallBack getCarTypeCallBack){
        this.context = context;
        this.api_key = api_key;
        this.getCarTypeCallBack = getCarTypeCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Получаю список категорий...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<List<CarType>> doInBackground(Void... params) {
        try {
            return App.getApi().getCarType(api_key).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<List<CarType>> response) {
        super.onPostExecute(response);
        progressDialog.dismiss();
        boolean result = false;
        List<CarType> data = new ArrayList<>();
        if(response == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже.", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (response.code()){
            case STATUS.Ok:{
                result = true;
                data = response.body();
                break;
            }
            case STATUS.NotFound:{
                Toast.makeText(context, "Данна категория не зарегистрирована!", Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.Unauthorized:{
                Toast.makeText(context, "Вы не авторизованы в системе!", Toast.LENGTH_SHORT).show();
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        getCarTypeCallBack.completed(result, data);
    }
}
