package com.example.evacuateme.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.evacuateme.Activity.InfoOrderActivity;
import com.example.evacuateme.AsyncTask.GetCarTypeAsync;
import com.example.evacuateme.Interface.GetCarTypeCallBack;
import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.R;
import com.google.gson.Gson;

import java.util.List;

public class StartFragment extends Fragment {
    private Button search_evacuator_BTN;
    private SharedPreferences sharedPreferences;

    public StartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        search_evacuator_BTN = (Button) view.findViewById(R.id.search_evacuator_BTN);
        search_evacuator_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getContext().getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                String api_key = sharedPreferences.getString("api_key", "");
                GetCarTypeAsync getCarTypeAsync = new GetCarTypeAsync(getContext(), api_key,
                        new GetCarTypeCallBack() {
                            @Override
                            public void completed(boolean result, List<CarType> data) {
                                if(result){
                                    Intent intent = new Intent(getContext(), InfoOrderActivity.class);
                                    intent.putExtra("car_list", new Gson().toJson(data));
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(getContext(), "Не удалось получить список категорий заказа!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                getCarTypeAsync.execute();
            }
        });
        return view;
    }
}
