package com.example.edriver.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.example.edriver.AsyncTask.ChangeStatusAsync;
import com.example.edriver.Interface.ChangeStatusCallBack;
import com.example.edriver.R;
import com.example.edriver.Service.GetOrderService;
import com.example.edriver.Service.UpdateLocationService;
import com.example.edriver.Utils.Order;
import com.example.edriver.Utils.STATUS;

public class StartFragment extends Fragment {
    private ToggleButton change_status_BTN;
    private SharedPreferences sharedPreferences;

    public StartFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        sharedPreferences = getActivity().getSharedPreferences("STATUS", Context.MODE_PRIVATE);
        change_status_BTN = (ToggleButton) view.findViewById(R.id.change_status_BTN);
        change_status_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent_order = new Intent(getActivity(), GetOrderService.class);
                final Intent intent_location = new Intent(getActivity(), UpdateLocationService.class);
                if(change_status_BTN.isChecked()){
                    final ChangeStatusAsync changeStatusAsync = new ChangeStatusAsync(getContext(), STATUS.Working,
                            new ChangeStatusCallBack() {
                                @Override
                                public void completed(boolean result) {
                                    if(result){
                                        change_status_BTN.setChecked(true);
                                        SharedPreferences.Editor editor_status = sharedPreferences.edit();
                                        editor_status.putInt("status", STATUS.Working);
                                        editor_status.apply();
                                        getActivity().startService(intent_order);
                                        getActivity().startService(intent_location);
                                    }
                                    else {
                                        change_status_BTN.setChecked(false);
                                        SharedPreferences.Editor editor_status = sharedPreferences.edit();
                                        editor_status.putInt("status", STATUS.NotWorking);
                                        editor_status.apply();
                                    }
                                }
                            });
                    changeStatusAsync.execute();
                }
                else {
                    final ChangeStatusAsync changeStatusAsync = new ChangeStatusAsync(getContext(), STATUS.NotWorking,
                            new ChangeStatusCallBack() {
                                @Override
                                public void completed(boolean result) {
                                    if(result){
                                        change_status_BTN.setChecked(false);
                                        SharedPreferences.Editor editor_status = sharedPreferences.edit();
                                        editor_status.putInt("status", STATUS.NotWorking);
                                        editor_status.apply();
                                        getActivity().stopService(intent_order);
                                        getActivity().stopService(intent_location);
                                    }
                                    else {
                                        change_status_BTN.setChecked(true);
                                        SharedPreferences.Editor editor_status = sharedPreferences.edit();
                                        editor_status.putInt("status", STATUS.Working);
                                        editor_status.apply();
                                    }
                                }
                            });
                    changeStatusAsync.execute();
                }
            }
        });
        if(sharedPreferences.getInt("status", 0)==STATUS.NotWorking){
            change_status_BTN.setChecked(false);
        }
        else {
            change_status_BTN.setChecked(true);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
