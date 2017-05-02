package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.STATUS;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class EstimateOrderAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {
    private Context context;
    private String api_key;
    private int order_id;
    private int rate;
    private ProgressDialog progressDialog;

    public EstimateOrderAsync(Context context, String api_key, int order_id, int rate){
        this.context = context;
        this.api_key = api_key;
        this.order_id = order_id;
        this.rate = rate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Завершение заказа...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        try {
            return App.getApi().estimateOrder(api_key, order_id, rate).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<ResponseBody> responseBody) {
        super.onPostExecute(responseBody);
        boolean result = false;
        progressDialog.dismiss();

        if(responseBody==null){
            return;
        }

        switch (responseBody.code()){

            case STATUS.BadRequest:{
                break;
            }

            case STATUS.Unauthorized:{
                break;
            }

            case STATUS.Ok:{
                break;
            }
        }
    }
}
