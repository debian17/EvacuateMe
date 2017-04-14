package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.evacuateme.Interface.SignUpCallBack;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.RESPONSE;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 14-Apr-17.
 */

public class SignUpAsync extends AsyncTask<Void, Void, Response<String>> {

    private Context context;
    private String phoneNumber;
    private String name;
    private String code;
    private SignUpCallBack signUpCallBack;
    private ProgressDialog progressDialog;

    public SignUpAsync(Context context, String phoneNumber, String name, String code, SignUpCallBack signUpCallBack){
        this.context = context;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.code = code;
        this.signUpCallBack = signUpCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Регистрирую в системе...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<String> doInBackground(Void... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phoneNumber);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("name", name);
        try {
            return App.getApi().signUp(jsonObject).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<String> responseBodyResponse) {
        super.onPostExecute(responseBodyResponse);
        progressDialog.dismiss();
        boolean res = false;

        if(responseBodyResponse == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if(responseBodyResponse.code() == RESPONSE.Ok){
            Log.d("TAG", responseBodyResponse.body());
            res = true;
        }
        else {
            res = false;
        }
        signUpCallBack.completed(res);
    }
}
