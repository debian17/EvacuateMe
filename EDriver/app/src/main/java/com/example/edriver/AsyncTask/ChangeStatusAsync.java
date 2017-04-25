package com.example.edriver.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.edriver.Interface.ChangeStatusCallBack;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.STATUS;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 17-Apr-17.
 */

public class ChangeStatusAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {
    private Context context;
    private int new_status;
    private ChangeStatusCallBack changeStatusCallBack;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private String api_key;

    public ChangeStatusAsync(Context context, int new_status, ChangeStatusCallBack changeStatusCallBack){
        this.context = context;
        this.new_status = new_status;
        this.changeStatusCallBack = changeStatusCallBack;
        sharedPreferences = context.getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        api_key = sharedPreferences.getString("api_key", "");
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Меняю Ваш статус...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        try {
            return App.getApi().change_worker_status(api_key, new_status).execute();
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
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        switch (responseBody.code()){
            case STATUS.BadRequest:{
                Toast.makeText(context, "Неверный запрос! Не могу обновить статус!",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.Unauthorized:{
                Toast.makeText(context, "Неверный запрос! Не могу обновить статус!",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case STATUS.Ok:{
                Toast.makeText(context, "Ваш статус обновлен!",
                        Toast.LENGTH_SHORT).show();
                result = true;
                break;
            }
        }
        changeStatusCallBack.completed(result);
    }
}
