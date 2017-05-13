package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 13-May-17.
 */

public class ChangeSettingsAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {
    private Context context;
    private String api_key;
    private String car_model;
    private String car_colour;
    private ProgressDialog progressDialog;

    public ChangeSettingsAsync(Context context, String api_key, String car_model, String car_colour){
        this.context = context;
        this.api_key = api_key;
        this.car_model = car_model;
        this.car_colour = car_colour;
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
        return null;
    }

    @Override
    protected void onPostExecute(Response<ResponseBody> responseBodyResponse) {
        super.onPostExecute(responseBodyResponse);
    }
}
