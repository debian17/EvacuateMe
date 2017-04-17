package com.example.edriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edriver.AsyncTask.SignInAsync;
import com.example.edriver.Interface.SignInCallBack;
import com.example.edriver.R;

public class LogInActivity extends AppCompatActivity {
    private EditText input_code_ET;
    private Button signIn_BTN;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        final Intent intent = getIntent();
        input_code_ET = (EditText) findViewById(R.id.input_code_ET);
        signIn_BTN = (Button) findViewById(R.id.signIn_BTN);
        signIn_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCode()){
                    SignInAsync signInAsync = new SignInAsync(LogInActivity.this,
                            intent.getStringExtra("phoneNumber"),
                            input_code_ET.getText().toString(), new SignInCallBack() {
                        @Override
                        public void completed(boolean result, String api_key) {
                            if(result){
                                sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor_key = sharedPreferences.edit();
                                editor_key.putString("api_key", api_key);
                                editor_key.apply();
                                sharedPreferences = getSharedPreferences("IS_LOGIN", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor_isLogin = sharedPreferences.edit();
                                editor_isLogin.putBoolean("is_login", true);
                                editor_isLogin.apply();
                                Intent intent = new Intent(LogInActivity.this, NavigationDrawerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LogInActivity.this, "Не удалось выполнить вход!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    signInAsync.execute();
                }
                else {
                    Toast.makeText(LogInActivity.this, "Введите код!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkCode(){
        if(input_code_ET.getText().toString().equals("")){
            return false;
        }
        else {
            return true;
        }
    }
}
