package com.example.edriver.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.edriver.R;
import com.example.edriver.Utils.Order;

public class OnTheWayFragment extends Fragment {
    private Button i_am_here_BTN;
    private Button call_client_BTN;

    public OnTheWayFragment() {
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

            }
        });

        call_client_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CALL", order.getPhone());
            }
        });

        return view;
    }
}
