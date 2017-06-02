package com.example.evacuateme.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.evacuateme.R;

public class PerformingFragment extends Fragment {
    private Button complete_order_BTN;


    public PerformingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_performing, container, false);
        complete_order_BTN = (Button)view.findViewById(R.id.complete_order_BTN);


        return view;
    }
}
