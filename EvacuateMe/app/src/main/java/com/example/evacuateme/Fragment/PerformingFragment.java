package com.example.evacuateme.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.evacuateme.AsyncTask.ChangeOrderStatusAsync;
import com.example.evacuateme.Interface.ChangeOrderStatusCallBack;
import com.example.evacuateme.Model.OrderHistory;
import com.example.evacuateme.R;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.MyAction;
import com.example.evacuateme.Utils.STATUS;
import com.example.evacuateme.Utils.Worker;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformingFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private Button complete_order_BTN;
    private Worker worker;


    public PerformingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_performing, container, false);
        complete_order_BTN = (Button)view.findViewById(R.id.complete_order_BTN);

        complete_order_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ORDER", "CLICK COMPLETED");
                worker = Worker.getInstance();
                ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(getContext(),
                        worker.getOrder_id(), STATUS.Completed, new ChangeOrderStatusCallBack() {
                    @Override
                    public void completed(boolean result) {
                        Toast.makeText(getContext(), "Вы завершили заказ!", Toast.LENGTH_SHORT).show();
                        worker.setOrder_status(STATUS.Completed);
                        Intent intent = new Intent(MyAction.OrderCompleted);
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    }
                });
                changeOrderStatusAsync.execute();
            }
        });

        return view;
    }
}
