package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.evacuateme.Interface.IsUserExistsCallBack;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.RESPONSE;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 14-Apr-17.
 */

public class IsUserExistsAsync extends AsyncTask<Void, Void, Response<ResponseBody>> {
    private String phoneNumber;
    private ProgressDialog progressDialog;
    private Context context;
    private IsUserExistsCallBack isUserExistsCallBack;

    public IsUserExistsAsync(Context context, String phoneNumber, IsUserExistsCallBack isUserExistsCallBack){
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.isUserExistsCallBack = isUserExistsCallBack;
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
            return App.getApi().isUserExists(phoneNumber).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<ResponseBody> result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        boolean isExist;

        if(result == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if(result.code() == RESPONSE.Ok){
            isExist = true;
        }
        else{
            isExist = false;
        }
        isUserExistsCallBack.completed(isExist);
    }
}
