package com.example.evacuateme.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.evacuateme.AsyncTask.GetCarTypeAsync;
import com.example.evacuateme.Interface.GetCarTypeCallBack;
import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.R;

import java.util.List;

public class StartFragment extends Fragment {
    private Button search_evacuator_BTN;

    public StartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        search_evacuator_BTN = (Button) view.findViewById(R.id.search_evacuator_BTN);
        search_evacuator_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCarTypeAsync getCarTypeAsync = new GetCarTypeAsync(getContext(),
                        new GetCarTypeCallBack() {
                            @Override
                            public void completed(boolean result, List<CarType> data) {
                                if(result){
                                    Toast.makeText(getContext(), data.get(0).name, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                getCarTypeAsync.execute();
            }
        });
        return view;
    }
}
