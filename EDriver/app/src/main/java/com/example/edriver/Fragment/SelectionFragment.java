package com.example.edriver.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.edriver.AsyncTask.ChangeOrderStatusAsync;
import com.example.edriver.Interface.ChangeOrderStatusCallBack;
import com.example.edriver.R;
import com.example.edriver.Service.GetOrderService;
import com.example.edriver.Utils.Order;
import com.example.edriver.Utils.STATUS;

public class SelectionFragment extends Fragment {
    private Button confirm_order_BTN;
    private Button refuse_order_BTN;
    private FragmentTransaction fragmentTransaction;

    public SelectionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection, container, false);
        confirm_order_BTN = (Button) view.findViewById(R.id.confirm_order_BTN);
        refuse_order_BTN = (Button) view.findViewById(R.id.refuse_order_BTN);

        confirm_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = Order.getInstance();
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(),
                        true, order.getOrder_id(), 1, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {
                        if(result){
                            Log.d("ORDER", "УСПЕШНО ПОДТВЕРЖДЕН");
                        }
                        else {
                            Log.d("ORDER", "НЕ УДАЛОСЬ ПОДТВЕРДИТЬ");
                        }
                    }
                });
                changeOrderStatusAsync.execute();
            }
        });

        refuse_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = Order.getInstance();
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(),
                        false, order.getOrder_id(), 4, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {
                        if(result){
                            fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            StartFragment startFragment = new StartFragment();
                            fragmentTransaction.replace(R.id.info_container_fragment, startFragment).commit();
                            final Intent intent_order = new Intent(getActivity(), GetOrderService.class);
                            getActivity().startService(intent_order);
                        }
                        else {
                            Log.d("ORDER", "НЕ УДАЛОСЬ ОТВЕРГНУТЬ");
                        }
                    }
                });
                changeOrderStatusAsync.execute();
            }
        });

        return view;
    }
}
