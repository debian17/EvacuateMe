package com.example.edriver.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.edriver.AsyncTask.ChangeOrderStatusAsync;
import com.example.edriver.Interface.ChangeOrderStatusCallBack;
import com.example.edriver.R;
import com.example.edriver.Service.CheckOrderStatusService;
import com.example.edriver.Service.GetOrderService;
import com.example.edriver.Utils.MyAction;
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
        final Order order = Order.getInstance();

        confirm_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(),
                        order.getOrder_id(), Order.OnTheWay, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {
                        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        if(result){
                            OnTheWayFragment onTheWayFragment = new OnTheWayFragment();
                            fragmentTransaction.replace(R.id.info_container_fragment, onTheWayFragment).commit();
                            Intent intent = new Intent(getActivity(), CheckOrderStatusService.class);
                            getActivity().startService(intent);
                            Intent markers = new Intent(MyAction.DrawTwoMarks);
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(markers);
                        }
                        else {
                            Log.d("ORDER", "НЕ УДАЛОСЬ ПОДТВЕРДИТЬ");
                            Toast.makeText(getContext(), "К сожалению клиент отменил заказ!", Toast.LENGTH_SHORT).show();
                            StartFragment startFragment = new StartFragment();
                            fragmentTransaction.replace(R.id.info_container_fragment, startFragment).commit();
                            Intent intent = new Intent(getActivity(), GetOrderService.class);
                            getActivity().startService(intent);
                        }
                    }
                });
                changeOrderStatusAsync.execute();
            }
        });

        refuse_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(),
                        order.getOrder_id(), Order.CanceledByWorker, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {
                        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        StartFragment startFragment = new StartFragment();
                        fragmentTransaction.replace(R.id.info_container_fragment, startFragment).commit();
                        final Intent intent_order = new Intent(getActivity(), GetOrderService.class);
                        getActivity().startService(intent_order);
                    }
                });
                changeOrderStatusAsync.execute();
            }
        });
        return view;
    }
}
