package com.example.evacuateme.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.evacuateme.R;

public class CarSettingsFragment extends Fragment {
    private EditText car_settings_model_ET;
    private EditText car_settings_colour_ET;
    private Button change_settings_BTN;
    public CarSettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_settings, container, false);
        car_settings_model_ET = (EditText) view.findViewById(R.id.car_settings_model_ET);
        car_settings_colour_ET = (EditText) view.findViewById(R.id.car_settings_colour_ET);
        change_settings_BTN = (Button) view.findViewById(R.id.change_settings_BTN);
        change_settings_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmptyET()){

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
