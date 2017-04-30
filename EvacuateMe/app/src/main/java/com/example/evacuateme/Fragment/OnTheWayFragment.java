package com.example.evacuateme.Fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.evacuateme.AsyncTask.ChangeOrderStatusAsync;
import com.example.evacuateme.Interface.ChangeOrderStatusCallBack;
import com.example.evacuateme.R;
import com.example.evacuateme.Service.ConfirmOrderService;
import com.example.evacuateme.Utils.MyAction;
import com.example.evacuateme.Utils.STATUS;
import com.example.evacuateme.Utils.Worker;


public class OnTheWayFragment extends Fragment {
    private Button refuse_order_BTN;
    private Button call_worker_BTN;
    private Worker worker;
    private FragmentTransaction fragmentTransaction;

    public OnTheWayFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_the_way, container, false);
        refuse_order_BTN = (Button) view.findViewById(R.id.refuse_order_BTN);
        call_worker_BTN = (Button) view.findViewById(R.id.call_worker_BTN);
        worker = Worker.getInstance();

        refuse_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(),
                        worker.getOrder_id(), STATUS.CanceledByClient, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {
                        Toast.makeText(getContext(), "Вы отменили заказ!", Toast.LENGTH_SHORT).show();
                        worker.setOrder_status(STATUS.CanceledByClient);
                        Intent intent = new Intent(MyAction.OrderCanceledByClient);
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    }
                });
                changeOrderStatusAsync.execute();
            }
        });

        call_worker_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri call = Uri.parse("tel:" + worker.getPhone());
                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                startActivity(surf);
            }
        });






        return view;
    }
}
