package com.example.edriver.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.edriver.Interface.ChangeOrderStatusCallBack;
import com.example.edriver.Interface.ChangeStatusCallBack;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.STATUS;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 24-Apr-17.
 */

public class ChangeOrderStatusAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {

    private Context context;
    private int order_id;
    private int new_status;
    private String api_key;
    private ChangeOrderStatusCallBack changeOrderStatusCallBack;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    public ChangeOrderStatusAsync(Context context, int order_id, int new_status, ChangeOrderStatusCallBack changeOrderStatusCallBack){
        this.context = context;
        this.order_id = order_id;
        this.new_status = new_status;
        this.changeOrderStatusCallBack = changeOrderStatusCallBack;
        sharedPreferences = context.getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", "");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Ожидайте...");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        try {
            return App.getApi().changeOrderStatus(api_key, order_id, new_status).execute();
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
            Toast.makeText(context, "Не удалось выполнить операцию!", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (responseBody.code()){
            case STATUS.BadRequest:{
                result = false;
                break;
            }
            case STATUS.NotFound:{
                result = false;
                //Toast.makeText(context, "Такой заказ не найден!", Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.Unauthorized:{
                result = false;
                //Toast.makeText(context, "Вы не авторизованы!", Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.Ok:{
                result = true;
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        changeOrderStatusCallBack.completed(result);
    }
}
