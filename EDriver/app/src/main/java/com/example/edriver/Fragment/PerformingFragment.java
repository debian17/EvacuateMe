package com.example.edriver.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.edriver.AsyncTask.ChangeOrderStatusAsync;
import com.example.edriver.Interface.ChangeOrderStatusCallBack;
import com.example.edriver.R;
import com.example.edriver.Service.CheckOrderStatusService;
import com.example.edriver.Utils.App;
import com.example.edriver.Utils.MyAction;
import com.example.edriver.Utils.MyLocation;
import com.example.edriver.Utils.Order;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformingFragment extends Fragment {
    private Button complete_order_BTN;
    private Order order;
    private SharedPreferences sharedPreferences;
    private MyLocation myLocation;

    public PerformingFragment() {
        order = Order.getInstance();
        myLocation = MyLocation.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_performing, container, false);
        complete_order_BTN = (Button) view.findViewById(R.id.complete_order_BTN);
        complete_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getContext().getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                String api_key = sharedPreferences.getString("api_key", "");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("latitude", myLocation.getLatitude());
                jsonObject.addProperty("longitude", myLocation.getLongitude());
                App.getApi().updateLocation(api_key, jsonObject).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(),
                                order.getOrder_id(), Order.Completed, new ChangeOrderStatusCallBack() {
                            @Override
                            public void completed(boolean result) {
                                Toast.makeText(getContext(), "Заказ завершен!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MyAction.OrderCompleted);
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                            }
                        });
                        changeOrderStatusAsync.execute();
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }
        });
        return view;
    }
}
