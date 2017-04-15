package com.example.evacuateme.Activity;

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

import com.example.evacuateme.AsyncTask.GetCodeAsync;
import com.example.evacuateme.AsyncTask.IsUserExistsAsync;
import com.example.evacuateme.Interface.GetCodeCallBack;
import com.example.evacuateme.Interface.IsUserExistsCallBack;
import com.example.evacuateme.R;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.Net;

public class MainActivity extends AppCompatActivity implements IsUserExistsCallBack, GetCodeCallBack {
    private SharedPreferences sharedPreferences;
    private Button start_BTN;
    private EditText phoneNumber_ET;
    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        sharedPreferences = getSharedPreferences("IS_LOGIN", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("is_login", false)){
            Log.d("TAG","SP = true");
            Intent intent = new Intent(MainActivity.this, NavigationDrawerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Log.d("TAG","SP = false");
            start_BTN = (Button) findViewById(R.id.start_BTN);
            phoneNumber_ET = (EditText) findViewById(R.id.phoneNumber_ET);
            start_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Net.isAvailable(MainActivity.this)){
                        Toast.makeText(MainActivity.this, "Для работы приложения необходимо интернет" +
                                " соединение!", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    if(!isNumberEmpty()){
                        IsUserExistsAsync isUserExistsAsync  = new IsUserExistsAsync(MainActivity.this,
                                phoneNumber_ET.getText().toString(), MainActivity.this);
                        isUserExistsAsync.execute();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Введите Ваш номер телефона!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    @Override
    public void completed(boolean isExist) {
        this.isExist = isExist;
        GetCodeAsync getCodeAsync = new GetCodeAsync(MainActivity.this,
                phoneNumber_ET.getText().toString(), MainActivity.this);
        getCodeAsync.execute();
    }

    private boolean isNumberEmpty(){
        if(phoneNumber_ET.getText().toString().equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void getCodeCallBack(boolean result) {
        if(result){
            Intent intent = new Intent(MainActivity.this, LogRegActivity.class);
            intent.putExtra("isExist", isExist);
            intent.putExtra("phoneNumber", phoneNumber_ET.getText().toString());
            startActivity(intent);
        }
    }
}
