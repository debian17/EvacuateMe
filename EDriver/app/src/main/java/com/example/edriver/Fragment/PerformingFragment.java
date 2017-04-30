package com.example.edriver.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.edriver.AsyncTask.ChangeOrderStatusAsync;
import com.example.edriver.Interface.ChangeOrderStatusCallBack;
import com.example.edriver.R;
import com.example.edriver.Utils.Order;

public class PerformingFragment extends Fragment {
    private Button complete_order_BTN;
    private Order order;

    public PerformingFragment() {
        order = Order.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_performing, container, false);
        complete_order_BTN = (Button) view.findViewById(R.id.complete_order_BTN);
        complete_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(), order.getOrder_id(),
                        Order.Completed, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {

                    }
                });
                   changeOrderStatusAsync.execute();
            }
        });
        return view;
    }
}
