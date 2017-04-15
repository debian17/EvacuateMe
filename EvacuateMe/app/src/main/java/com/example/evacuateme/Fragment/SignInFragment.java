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

import com.example.evacuateme.Activity.NavigationDrawerActivity;
import com.example.evacuateme.Activity.Test_LogReg;
import com.example.evacuateme.AsyncTask.SignInAsync;
import com.example.evacuateme.Interface.SignInCallBack;
import com.example.evacuateme.R;

public class SignInFragment extends Fragment {
    private EditText input_sms_ET;
    private Button signIn_BTN;
    private Bundle bundle;
    private SharedPreferences sharedPreferences;

    public SignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
        input_sms_ET = (EditText) v.findViewById(R.id.input_sms_ET);
        signIn_BTN = (Button) v.findViewById(R.id.signIn_BTN);
        bundle = getArguments();
        signIn_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCode()){
                    SignInAsync signInAsync = new SignInAsync(getContext(), bundle.getString("phoneNumber", ""),
                            input_sms_ET.getText().toString(),
                            new SignInCallBack() {
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
                                        Intent intent = new Intent(getContext(), NavigationDrawerActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Не удалось выполнить вход!", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                    signInAsync.execute();
                }
                else {
                    Toast.makeText(getContext(), "Поля кода не может быть пустым!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        return v;
    }

    private boolean checkCode(){
        if(input_sms_ET.getText().toString().equals("")){
            return false;
        }
        else {
            return true;
        }
    }

}
