package com.example.edriver.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import com.example.edriver.Utils.MyAction;
import com.example.edriver.Utils.Order;

public class OnTheWayFragment extends Fragment {
    private Button i_am_here_BTN;
    private Button call_client_BTN;
    private Order order;
    private FragmentTransaction fragmentTransaction;

    public OnTheWayFragment() {
        order = Order.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_the_way, container, false);

        i_am_here_BTN = (Button) view.findViewById(R.id.i_am_here_BTN);
        call_client_BTN = (Button) view.findViewById(R.id.call_client_BTN);

        final Order order = Order.getInstance();
        i_am_here_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(), order.getOrder_id(),
                        Order.Performing, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {
                        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        PerformingFragment performingFragment = new PerformingFragment();
                        fragmentTransaction.replace(R.id.info_container_fragment, performingFragment).commit();
                        Intent service_intent = new Intent(getActivity(), CheckOrderStatusService.class);
                        getActivity().stopService(service_intent);
                        Intent intent = new Intent(MyAction.StartedImplementation);
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    }
                });
                changeOrderStatusAsync.execute();
            }
        });

        call_client_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri call = Uri.parse("tel:" + order.getPhone());
                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                startActivity(surf);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
