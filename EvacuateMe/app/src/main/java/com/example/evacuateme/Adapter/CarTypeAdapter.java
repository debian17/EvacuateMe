package com.example.evacuateme.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evacuateme.Model.CarType;
import com.example.evacuateme.R;
import com.example.evacuateme.Utils.Client;

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

    public CarTypeAdapter(Context context, List<CarType> items){
        this.context = context;
        this.items = items;
        client = Client.getInstance();
    }

    private List<CarType> items;
    private Context context;
    private View elemView;
    private Client client;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_type_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CarType carType = items.get(position);
        holder.car_type_name.setText(carType.name);
        if(position==0){
            holder.itemView.setBackgroundColor(Color.GREEN);
            elemView = holder.itemView;
            client.setCar_type(carType.id);
            Log.d("TYPE", String.valueOf(carType.id));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elemView.setBackgroundColor(Color.WHITE);
                holder.itemView.setBackgroundColor(Color.GREEN);
                elemView = holder.itemView;
                client.setCar_type(carType.id);
                Log.d("TYPE", String.valueOf(carType.id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
