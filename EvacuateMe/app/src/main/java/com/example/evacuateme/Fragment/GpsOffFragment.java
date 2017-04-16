package com.example.evacuateme.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.evacuateme.R;
import com.example.evacuateme.Utils.Gps;
import com.example.evacuateme.Utils.Net;

public class GpsOffFragment extends Fragment {
    private Button reload_BTN;
    private FragmentTransaction fragmentTransaction;

    public GpsOffFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps_off, container, false);

        reload_BTN = (Button) view.findViewById(R.id.reload_BTN);
        reload_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Net.isAvailable(getContext()) && Gps.isAvailable(getContext())){
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    MainMapFragment mainMapFragment = new MainMapFragment();
                    fragmentTransaction.replace(R.id.main_container_fragment, mainMapFragment).commit();
                }
                else{
                    Toast.makeText(getContext(), "Для работа приложения включите интернет и GPS!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
