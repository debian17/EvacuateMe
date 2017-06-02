package com.example.edriver.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.edriver.AsyncTask.ChangeOrderStatusAsync;
import com.example.edriver.AsyncTask.ChangeStatusAsync;
import com.example.edriver.Interface.ChangeStatusCallBack;
import com.example.edriver.R;
import com.example.edriver.Service.GetOrderService;
import com.example.edriver.Utils.STATUS;

public class OrderInfoActivity extends AppCompatActivity {
    private Bundle bundle;
    private Button confirm_completion_BTN;
    private TextView info_order_id_TV;
    private TextView info_distance_TV;
    private TextView info_summary_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        setTitle("Информация о заказе");
        confirm_completion_BTN = (Button) findViewById(R.id.confirm_completion_BTN);
        info_distance_TV = (TextView) findViewById(R.id.info_distance_TV);
        info_order_id_TV = (TextView) findViewById(R.id.info_order_id_TV);
        info_summary_TV = (TextView) findViewById(R.id.info_summary_TV);

        Intent intent = getIntent();
        if(intent!=null){
            bundle = intent.getBundleExtra("data");
            info_distance_TV.setText("Дистанция = "+String.valueOf(bundle.getDouble("distance")/1000+"км"));
            info_summary_TV.setText("Сумма заказа = "+String.valueOf(bundle.getDouble("summary")+"руб"));
            info_order_id_TV.setText("Номер заказа: "+String.valueOf(bundle.getInt("order_id")));
        }
        else {
            String error = "Нет данных";
            info_distance_TV.setText(error);
            info_summary_TV.setText(error);
            info_order_id_TV.setText(error);
        }

        confirm_completion_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderInfoActivity.this, NavigationDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Intent service_intent = new Intent(OrderInfoActivity.this, GetOrderService.class);
                Log.d("S", "SERVICE_INTENT");
                startService(service_intent);
                ChangeStatusAsync changeStatusAsync = new ChangeStatusAsync(OrderInfoActivity.this, STATUS.Working,
                        new ChangeStatusCallBack() {
                            @Override
                            public void completed(boolean result) {
                            }
                        });
                changeStatusAsync.execute();
                startActivity(intent);
            }
        });
    }
}
