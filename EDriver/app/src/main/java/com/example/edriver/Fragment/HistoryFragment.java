package com.example.edriver.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edriver.Adapter.OrderHistoryAdapter;
import com.example.edriver.Model.OrderHistory;
import com.example.edriver.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView historyRV;
    private LinearLayoutManager linearLayoutManager;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<OrderHistory> items;
    private TextView history_title_TV;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        history_title_TV = (TextView) view.findViewById(R.id.history_title_TV);
        Bundle bundle = getArguments();
        if(bundle!=null){
            String temp = bundle.getString("list_orders");
            Type type = new TypeToken<List<OrderHistory>>(){}.getType();
            items = new Gson().fromJson(temp, type);
            if(!items.isEmpty()){
                Log.d("HF", items.get(0).beginning_time);
                history_title_TV.setText("История заказов");
                historyRV = (RecyclerView) view.findViewById(R.id.orders_history_RV);
                linearLayoutManager = new LinearLayoutManager(getContext());
                historyRV.setLayoutManager(linearLayoutManager);
                orderHistoryAdapter = new OrderHistoryAdapter(items);
                historyRV.setAdapter(orderHistoryAdapter);
            }
            else {
                history_title_TV.setText("Вы еще не совершили ни одного заказа");
            }
        }
        else {
            Toast.makeText(getContext(), "Не удалось отобразить историю заказов!", Toast.LENGTH_SHORT).show();
        }
//        imageView = (ImageView) view.findViewById(R.id.image);
//
//        Glide.with(getContext())
//                .load("http://www.pixelstalk.net/wp-content/uploads/2016/06/Black-Wallpaper-Android.png")
//                .error(R.drawable.common_full_open_on_phone)
//                .into(imageView);
        return view;
    }
}