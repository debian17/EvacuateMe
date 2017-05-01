package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.evacuateme.Interface.GetCompaniesCallBack;
import com.example.evacuateme.Model.Companies;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.Client;
import com.example.evacuateme.Utils.STATUS;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class GetCompaniesAsync extends AsyncTask<Void, Void, Response<List<Companies>>> {
    private Context context;
    private String api_key;
    private int car_type;
    private GetCompaniesCallBack getCompaniesCallBack;
    private ProgressDialog progressDialog;
    private Client client;

    public GetCompaniesAsync(Context context, String api_key, int car_type, GetCompaniesCallBack getCompaniesCallBack){
        this.context = context;
        this.api_key = api_key;
        this.car_type = car_type;
        this.getCompaniesCallBack = getCompaniesCallBack;
        client = Client.getInstance();
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Получаю список доступных компаний...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<List<Companies>> doInBackground(Void... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("latitude", client.getLatitude());
        jsonObject.addProperty("longitude", client.getLongitude());
        jsonObject.addProperty("car_type", car_type);
        try {
            return App.getApi().getCompanies(api_key, jsonObject).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<List<Companies>> resultResponse) {
        super.onPostExecute(resultResponse);
        progressDialog.dismiss();
        boolean result = false;
        List<Companies> list_companies = new ArrayList<>();

        if(resultResponse == null){
            Toast.makeText(context, "Сервер временно недоступен!", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (resultResponse.code()){
            case STATUS.Unauthorized:{
                Toast.makeText(context, "Вы неавторизованы в системе!", Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.BadRequest:{
                Toast.makeText(context, "Не можем распознать Ваше местоположение!", Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.Ok:{
                result = true;
                list_companies= resultResponse.body();
            }
        }
        getCompaniesCallBack.completed(result, list_companies);
    }
}
