package com.example.edriver.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.edriver.Interface.GetOrderHistoryCallBack;
import com.example.edriver.Model.OrderHistory;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class GetOrderHistoryAsync extends AsyncTask<Void, Void, Response<List<OrderHistory>>> {
    private Context context;
    private String api_key;
    private GetOrderHistoryCallBack getOrderHistoryCallBack;
    private ProgressDialog progressDialog;

    public GetOrderHistoryAsync(Context context, String api_key, GetOrderHistoryCallBack getOrderHistoryCallBack){
        this.context = context;
        this.api_key = api_key;
        this.getOrderHistoryCallBack = getOrderHistoryCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Получаю историю заказов...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<List<OrderHistory>> doInBackground(Void... params) {
        try {
            return App.getApi().getOrderHistory(api_key).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<List<OrderHistory>> responseBody) {
        super.onPostExecute(responseBody);
        progressDialog.dismiss();
        boolean result = false;
        List<OrderHistory> list_orders = new ArrayList<>();
        if(responseBody==null){
            Toast.makeText(context, "Не удалось получить историю заказов!", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (responseBody.code()){
            case STATUS.Ok:{
                list_orders = responseBody.body();
                result = true;
                break;
            }
            case STATUS.NotFound:{
                result = true;
                break;
            }
            case STATUS.Unauthorized:{
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя оишбка сервера!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        getOrderHistoryCallBack.completed(result, list_orders);
    }
}
