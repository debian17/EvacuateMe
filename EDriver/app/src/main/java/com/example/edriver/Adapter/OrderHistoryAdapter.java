package com.example.edriver.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.edriver.Model.OrderHistory;
import com.example.edriver.R;

import java.util.List;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id;

        public ViewHolder(View ItemView) {
            super(ItemView);
            order_id = (TextView) itemView.findViewById(R.id.history_order_id_TV);
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
        holder.order_id.setText(String.valueOf(order.order_id));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}