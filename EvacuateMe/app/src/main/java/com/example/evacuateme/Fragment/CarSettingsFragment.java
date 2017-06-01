package com.example.evacuateme.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.evacuateme.AsyncTask.ChangeSettingsAsync;
import com.example.evacuateme.Interface.ChangeSettingsCallBack;
import com.example.evacuateme.R;

public class CarSettingsFragment extends Fragment {
    private EditText car_settings_model_ET;
    private EditText car_settings_colour_ET;
    private Button change_settings_BTN;
    private SharedPreferences sharedPreferences;
    public CarSettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_settings, container, false);
        car_settings_model_ET = (EditText) view.findViewById(R.id.car_settings_model_ET);
        car_settings_colour_ET = (EditText) view.findViewById(R.id.car_settings_colour_ET);
        change_settings_BTN = (Button) view.findViewById(R.id.change_settings_BTN);

        sharedPreferences = getContext().getSharedPreferences("CAR_SETTINGS", Context.MODE_PRIVATE);
        car_settings_model_ET.setText(sharedPreferences.getString("car_model", ""));
        car_settings_colour_ET.setText(sharedPreferences.getString("car_colour", ""));

        change_settings_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmptyET()){
                    sharedPreferences = getContext().getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                    String api_key = sharedPreferences.getString("api_key", "");
                    ChangeSettingsAsync changeSettingsAsync = new ChangeSettingsAsync(getContext(), api_key,
                            car_settings_model_ET.getText().toString(), car_settings_colour_ET.getText().toString(),
                            new ChangeSettingsCallBack() {
                                @Override
                                public void completed(boolean result) {
                                    if(result){
                                        sharedPreferences = getContext().getSharedPreferences("CAR_SETTINGS", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor_car_model = sharedPreferences.edit();
                                        editor_car_model.putString("car_model", car_settings_model_ET.getText().toString());
                                        editor_car_model.apply();
                                        SharedPreferences.Editor editor_car_colour = sharedPreferences.edit();
                                        editor_car_colour.putString("car_colour", car_settings_colour_ET.getText().toString());
                                        editor_car_colour.apply();
                                        Toast.makeText(getContext(), "Настройки успешно изменены!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Не удалось изменить настройки!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    changeSettingsAsync.execute();
                }
                else {
                    Toast.makeText(getContext(), "Все поля должны быть заполнены!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean isEmptyET(){
        if(car_settings_model_ET.getText().toString().equals("") || car_settings_colour_ET.getText().toString().equals("")){
            return true;
        }
        else {
            return false;
        }
    }
}
