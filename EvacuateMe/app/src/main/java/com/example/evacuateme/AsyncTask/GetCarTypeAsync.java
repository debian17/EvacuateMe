package com.example.evacuateme.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.evacuateme.Interface.GetCarTypeCallBack;
import com.example.evacuateme.Interface.GetCodeCallBack;
import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Андрей Кравченко on 17-Apr-17.
 */

public class GetCarTypeAsync extends AsyncTask<Void, Void, Response<List<CarType>>> {
    private Context context;
    private ProgressDialog progressDialog;
    private GetCarTypeCallBack getCarTypeCallBack;

    public GetCarTypeAsync(Context context, GetCarTypeCallBack getCarTypeCallBack){
        this.context = context;
        this.getCarTypeCallBack = getCarTypeCallBack;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Получаю список категорий...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Response<List<CarType>> doInBackground(Void... params) {
        try {
            return App.getApi().get_car_type().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response<List<CarType>> response) {
        super.onPostExecute(response);
        progressDialog.dismiss();
        boolean result = false;
        List<CarType> data = new ArrayList<>();
        if(response == null){
            Toast.makeText(context, "Сервер временно недоступен. Попробуйте позже.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        switch (response.code()){
            case STATUS.Ok:{
                result = true;
                data = response.body();
                break;
            }
            case STATUS.NotFound:{
                result = false;
                break;
            }
            default:{
                Toast.makeText(context, "Внутренняя ошибка сервера.", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
        }
        getCarTypeCallBack.completed(result, data);
    }
}
