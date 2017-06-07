package com.example.evacuateme.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.evacuateme.Model.OrderHistory;
import com.example.evacuateme.R;

import java.util.List;
import java.util.StringTokenizer;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView history_company_name_TV;
        private TextView order_history_id_TV;
        private TextView summ_history_TV;
        private TextView distance_history_TV;
        private TextView car_type_history_TV;
        private TextView phone_history_TV;
        private RatingBar history_rate_RB;

        public ViewHolder(View ItemView) {
            super(ItemView);
            history_company_name_TV = (TextView) itemView.findViewById(R.id.history_company_name_TV);
            order_history_id_TV = (TextView) itemView.findViewById(R.id.order_history_id_TV);
            summ_history_TV = (TextView) itemView.findViewById(R.id.summ_history_TV);
            distance_history_TV = (TextView) itemView.findViewById(R.id.distance_history_TV);
            car_type_history_TV = (TextView) itemView.findViewById(R.id.car_type_history_TV);
            phone_history_TV = (TextView) itemView.findViewById(R.id.phone_history_TV);
            history_rate_RB = (RatingBar) itemView.findViewById(R.id.history_rate_RB);
        }
    }

    private List<OrderHistory> items;

    public OrderHistoryAdapter(List<OrderHistory> items){
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_item, parent, false);
        OrderHistoryAdapter.ViewHolder viewHolder = new OrderHistoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderHistory order = items.get(position);
        holder.history_company_name_TV.setText(order.company);
        holder.order_history_id_TV.setText("Номер заказа: "+ String.valueOf(order.order_id));
        if(order.summary == null){
            holder.summ_history_TV.setText("Стоимость: 0руб");
        }
        else {
            holder.summ_history_TV.setText("Стоимость: "+ String.valueOf(order.summary)+ "руб");
        }
        if(order.distance == null){
            holder.distance_history_TV.setText("Расстояние: 0км");
        }
        else {
            holder.distance_history_TV.setText("Расстояние: "+ String.valueOf(order.distance/1000)+" км");
        }
        holder.car_type_history_TV.setText("Тип автомобиля: "+ order.car_type);
        holder.phone_history_TV.setText("Тел. компании: "+ order.contact_phone);
        holder.history_rate_RB.setIsIndicator(true);
        if(order.rate == null){
            holder.history_rate_RB.setRating(0);
        }
        else {
            if(order.rate == 0){
                holder.history_rate_RB.setRating(0);
            }
            else {
                holder.history_rate_RB.setRating(order.rate.floatValue());
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}