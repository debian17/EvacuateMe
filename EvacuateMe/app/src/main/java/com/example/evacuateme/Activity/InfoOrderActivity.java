package com.example.evacuateme.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.evacuateme.Adapter.CarTypeAdapter;
import com.example.evacuateme.AsyncTask.GetCompaniesAsync;
import com.example.evacuateme.Interface.GetCompaniesCallBack;
import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.Model.Companies;
import com.example.evacuateme.R;
import com.example.evacuateme.Utils.Client;
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
    private SharedPreferences sharedPreferences;
    private EditText car_model_ET;
    private EditText car_colour_ET;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_order);
        setTitle("Формирование заказа");
        client = Client.getInstance();
        Intent intent = getIntent();
        if(intent !=null){
            get_list_evacuator_BTN = (Button) findViewById(R.id.get_list_evacuator_BTN);
            category_RV = (RecyclerView) findViewById(R.id.category_RV);
            car_model_ET = (EditText) findViewById(R.id.car_model_ET);
            car_colour_ET = (EditText) findViewById(R.id.car_colour_ET);

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
                    if(!isEmptyET()){
                        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                        String api_key = sharedPreferences.getString("api_key", "");
                        GetCompaniesAsync getCompaniesAsync = new GetCompaniesAsync(InfoOrderActivity.this,
                                api_key, client.getCar_type(), new GetCompaniesCallBack() {
                            @Override
                            public void completed(boolean result, List<Companies> list_companies) {
                                if(result){
                                    Intent intent = new Intent(InfoOrderActivity.this, CompaniesListActivity.class);
                                    intent.putExtra("list_companies", new Gson().toJson(list_companies));
                                    client.setCar_model(car_model_ET.getText().toString());
                                    client.setCar_colour(car_colour_ET.getText().toString());
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(InfoOrderActivity.this, "Для данной категории нет свободных эвакуаторов!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        getCompaniesAsync.execute();
                    }
                    else {
                        Toast.makeText(InfoOrderActivity.this, "Для заказа необходимо заполнить все поля!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    }

    private boolean isEmptyET(){
        if(car_model_ET.getText().toString().equals("") || car_colour_ET.getText().toString().equals("")){
            return true;
        }
        else {
            return false;
        }
    }
}
