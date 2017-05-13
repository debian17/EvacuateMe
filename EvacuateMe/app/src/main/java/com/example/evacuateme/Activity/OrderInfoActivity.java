package com.example.evacuateme.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.evacuateme.AsyncTask.EstimateOrderAsync;
import com.example.evacuateme.R;

public class OrderInfoActivity extends AppCompatActivity {
    private Bundle bundle;
    private Button confirm_completion_BTN;
    private TextView info_order_id_TV;
    private TextView info_distance_TV;
    private TextView info_summary_TV;
    private TextView info_company_TV;
    private RatingBar ratingBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        confirm_completion_BTN = (Button) findViewById(R.id.confirm_completion_BTN);
        info_company_TV = (TextView) findViewById(R.id.info_company_TV);
        info_distance_TV = (TextView) findViewById(R.id.info_distance_TV);
        info_order_id_TV = (TextView) findViewById(R.id.info_order_id_TV);
        info_summary_TV = (TextView) findViewById(R.id.info_summary_TV);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        Intent intent = getIntent();
        if(intent!=null){
            bundle = intent.getBundleExtra("data");
            info_company_TV.setText(bundle.getString("company"));
            info_distance_TV.setText("Дистанция = "+String.valueOf(bundle.getDouble("distance")/1000)+"КМ");
            info_summary_TV.setText("Сумма заказа = "+String.valueOf(bundle.getDouble("summary")+"руб"));
            info_order_id_TV.setText("Номер заказа: "+String.valueOf(bundle.getInt("order_id")));
        }
        else {
            String error = "Нет данных";
            info_company_TV.setText(error);
            info_distance_TV.setText(error);
            info_summary_TV.setText(error);
            info_order_id_TV.setText(error);
        }

        confirm_completion_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Math.round(ratingBar.getRating())!=0){
                    sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                    String api_key = sharedPreferences.getString("api_key", "");
                    EstimateOrderAsync estimateOrderAsync = new EstimateOrderAsync(OrderInfoActivity.this,
                            api_key, bundle.getInt("order_id"), Math.round(ratingBar.getRating()));
                    estimateOrderAsync.execute();
                }
                Intent intent = new Intent(OrderInfoActivity.this, NavigationDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
