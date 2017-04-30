package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.evacuateme.Interface.CreateOrderCallBack;
import com.example.evacuateme.Model.OrderData;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.Client;
import com.example.evacuateme.Utils.STATUS;
import com.example.evacuateme.Utils.Worker;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 21-Apr-17.
 */

public class CreateOrderAsync extends AsyncTask<Void, Void, Response<OrderData>> {

    private ProgressDialog progressDialog;
    private Context context;
    private String api_ley;
    private Client client;
    private Worker worker;
    private CreateOrderCallBack createOrderCallBack;

    public CreateOrderAsync(Context context, String api_key, CreateOrderCallBack createOrderCallBack){
        this.context = context;
        this.api_ley = api_key;
        this.createOrderCallBack = createOrderCallBack;
        client = Client.getInstance();
        worker = Worker.getInstance();
    }


    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Формирую заказ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<OrderData> doInBackground(Void... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("latitude", client.getLatitude());
        jsonObject.addProperty("longitude", client.getLongitude());
        jsonObject.addProperty("car_type", client.getCar_type());
        jsonObject.addProperty("company_id", worker.getCompany_id());
        jsonObject.addProperty("worker_id", worker.getWorker_id());
        jsonObject.addProperty("commentary", client.getComment());
        try {
            return App.getApi().create_order(api_ley, jsonObject).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<OrderData> resultResponse) {
        super.onPostExecute(resultResponse);
        progressDialog.dismiss();
        boolean result = false;
        OrderData worker = null;

        if(resultResponse == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        switch (resultResponse.code()){

            case STATUS.Unauthorized:{
                Toast.makeText(context, "Вы неавторизованы в системе!", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
            case STATUS.BadRequest:{
                Toast.makeText(context, "Неверно отправлены данные. Поробуйте еще раз!", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
            case STATUS.NotFound:{
                Toast.makeText(context, "Данный работник уже занят. Попробуйте еще раз!", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
            case STATUS.Created:{
                result = true;
                worker = resultResponse.body();
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера!", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
        }
        createOrderCallBack.created(result, worker);
    }
}
