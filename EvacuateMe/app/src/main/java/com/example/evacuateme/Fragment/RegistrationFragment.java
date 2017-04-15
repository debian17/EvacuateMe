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
import android.widget.EditText;
import android.widget.Toast;

import com.example.evacuateme.Activity.Test_LogReg;
import com.example.evacuateme.AsyncTask.SignUpAsync;
import com.example.evacuateme.Interface.SignUpCallBack;
import com.example.evacuateme.R;

public class RegistrationFragment extends Fragment {
    private EditText input_name_ET;
    private EditText input_reg_sms_ET;
    private Button signUp_BTN;
    private Bundle bundle;
    private SharedPreferences sharedPreferences;

    public RegistrationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration, container, false);
        input_name_ET = (EditText) v.findViewById(R.id.input_name_ET);
        input_reg_sms_ET = (EditText) v.findViewById(R.id.input_reg_sms_ET);
        signUp_BTN = (Button) v.findViewById(R.id.signUp_BTN);
        bundle = getArguments();
        signUp_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkName() && checkCode()){
                    SignUpAsync signUpAsync = new SignUpAsync(getContext(),
                            bundle.getString("phoneNumber", ""),
                            input_name_ET.getText().toString(),
                            input_reg_sms_ET.getText().toString(), new SignUpCallBack() {
                        @Override
                        public void completed(boolean result, String api_key) {
                            if(result){
                                sharedPreferences = getContext().getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor_key = sharedPreferences.edit();
                                editor_key.putString("api_key", api_key);
                                Log.d("TAG API_KEY = ", api_key);
                                editor_key.apply();
                                sharedPreferences = getContext().getSharedPreferences("IS_LOGIN", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor_isLogin = sharedPreferences.edit();
                                editor_isLogin.putBoolean("is_login", true);
                                editor_isLogin.apply();
                                Intent intent = new Intent(getContext(), Test_LogReg.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getContext(), "Регистрация не удалась!", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }
                    });
                    signUpAsync.execute();
                }
                else {
                    Toast.makeText(getContext(), "Все поля должны быть заполнены!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return v;
    }

    private boolean checkName(){
        if(input_name_ET.getText().toString().equals("")){
            return false;
        }
        else {
            return true;
        }
    }

    private boolean checkCode(){
        if(input_reg_sms_ET.getText().toString().equals("")){
            return false;
        }
        else {
            return true;
        }
    }
}
