package com.example.edriver.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edriver.AsyncTask.GetCodeAsync;
import com.example.edriver.AsyncTask.IsUserExistAsync;
import com.example.edriver.Interface.GetCodeCallBack;
import com.example.edriver.Interface.IsUserExistCallBack;
import com.example.edriver.R;
import com.example.edriver.Utils.Net;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText input_phone_ET;
    private Button get_code_BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        sharedPreferences = getSharedPreferences("IS_LOGIN", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("is_login", false)){
            Intent intent = new Intent(MainActivity.this, NavigationDrawerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            input_phone_ET = (EditText) findViewById(R.id.input_phone_ET);
            get_code_BTN = (Button) findViewById(R.id.get_code_BTN);
            get_code_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Net.isAvailable(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "Для работы приложения необходимо интернет" +
                                " соединение!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!isNumberEmpty()){
                        IsUserExistAsync isUserExistAsync = new IsUserExistAsync(MainActivity.this,
                                input_phone_ET.getText().toString(), new IsUserExistCallBack() {
                            @Override
                            public void completed(boolean isExist) {
                                if(isExist){
                                    GetCodeAsync getCodeAsync = new GetCodeAsync(MainActivity.this,
                                            input_phone_ET.getText().toString(), new GetCodeCallBack() {
                                        @Override
                                        public void getCodeCallBack(boolean result) {
                                            if(result){
                                                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                                                intent.putExtra("phoneNumber", input_phone_ET.getText().toString());
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                    getCodeAsync.execute();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Данный номер телефона " +
                                            "не зарегистрирован в системе!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        isUserExistAsync.execute();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Введите Ваш номер телефона!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(MainActivity.this, "Извините, но для использования приложения " +
                        "необходимо предоставить разрешения!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private boolean isNumberEmpty(){
        if(input_phone_ET.getText().toString().equals("")){
            return true;
        }
        else{
            return false;
        }
    }
}
