package com.example.evacuateme.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.Model.Firm;
import com.example.evacuateme.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Андрей Кравченко on 18-Apr-17.
 */

public class CarTypeAdapter extends RecyclerView.Adapter<CarTypeAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView car_type_name;

        public ViewHolder(View ItemView){
            super(ItemView);
            car_type_name = (TextView) itemView.findViewById(R.id.car_type_TV);
        }
    }

    public CarTypeAdapter(List<CarType> items){
        this.items = items;
    }

    private List<CarType> items;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_type_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CarType carType = items.get(position);
        holder.car_type_name.setText(String.valueOf(carType.name));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
