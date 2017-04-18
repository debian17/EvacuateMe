package com.example.evacuateme.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.evacuateme.Adapter.CarTypeAdapter;
import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.R;
import com.example.evacuateme.Utils.MyLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class InfoOrderActivity extends AppCompatActivity {
    private List<CarType> items;
    private Button get_list_evacuator_BTN;
    private RecyclerView category_RV;
    private LinearLayoutManager linearLayoutManager;
    private CarTypeAdapter carTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_order);
        Intent intent = getIntent();
        if(intent !=null){
            get_list_evacuator_BTN = (Button) findViewById(R.id.get_list_evacuator_BTN);
            category_RV = (RecyclerView) findViewById(R.id.category_RV);

            linearLayoutManager = new LinearLayoutManager(this);
            category_RV.setLayoutManager(linearLayoutManager);

            String temp = intent.getStringExtra("car_list");
            Type type = new TypeToken<List<CarType>>(){}.getType();
            items = new Gson().fromJson(temp, type);

            carTypeAdapter = new CarTypeAdapter(items);
            category_RV.setAdapter(carTypeAdapter);

            get_list_evacuator_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
