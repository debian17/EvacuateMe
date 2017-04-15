package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.evacuateme.Interface.GetCodeCallBack;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.RESPONSE;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 14-Apr-17.
 */

public class GetCodeAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {
    private Context context;
    private String phoneNumber;
    private GetCodeCallBack getCodeCallBack;
    private ProgressDialog progressDialog;

    public GetCodeAsync(Context context, String phoneNumber, GetCodeCallBack getCodeCallBack){
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.getCodeCallBack = getCodeCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Получаю смс код...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<ResponseBody> doInBackground(Void... params) {
        try {
            return App.getApi().get_code(phoneNumber).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<ResponseBody> stringResponse) {
        super.onPostExecute(stringResponse);
        progressDialog.dismiss();
        boolean result = false;
        if(stringResponse == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже!",
                   Toast.LENGTH_SHORT).show();
           return;
        }
        switch (stringResponse.code()){
            case RESPONSE.Ok:{
                result = true;
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера.",
                        Toast.LENGTH_SHORT).show();
                break;
            }
        }
        getCodeCallBack.getCodeCallBack(result);
    }
}
