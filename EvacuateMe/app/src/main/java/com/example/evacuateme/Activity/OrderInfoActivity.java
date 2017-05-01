package com.example.evacuateme.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.evacuateme.R;

public class OrderInfoActivity extends AppCompatActivity {
    private Bundle bundle;
    private Button confirm_completion_BTN;
    private TextView info_order_id_TV;
    private TextView info_distance_TV;
    private TextView info_summary_TV;
    private TextView info_company_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        confirm_completion_BTN = (Button) findViewById(R.id.confirm_completion_BTN);
        info_company_TV = (TextView) findViewById(R.id.info_company_TV);
        info_distance_TV = (TextView) findViewById(R.id.info_distance_TV);
        info_order_id_TV = (TextView) findViewById(R.id.info_order_id_TV);
        info_summary_TV = (TextView) findViewById(R.id.info_summary_TV);

        Intent intent = getIntent();
        if(intent!=null){
            bundle = intent.getBundleExtra("data");
            info_company_TV.setText(bundle.getString("company"));
            info_distance_TV.setText(String.valueOf(bundle.getDouble("distance")));
            info_summary_TV.setText(String.valueOf(bundle.getDouble("summary")));
            info_order_id_TV.setText(String.valueOf(bundle.getInt("order_id")));
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
                Intent intent = new Intent(OrderInfoActivity.this, NavigationDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
