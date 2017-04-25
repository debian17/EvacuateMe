package com.example.evacuateme.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.evacuateme.AsyncTask.CreateOrderAsync;
import com.example.evacuateme.Interface.CreateOrderCallBack;
import com.example.evacuateme.Model.Companies;
import com.example.evacuateme.Model.Worker;
import com.example.evacuateme.R;
import com.example.evacuateme.Service.ConfirmOrderService;
import com.example.evacuateme.Utils.Client;

import java.util.List;

/**
 * Created by Андрей Кравченко on 20-Apr-17.
 */

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView company_name;
        private TextView tariff;
        private TextView duration;
        private Button make_order;

        public ViewHolder(View ItemView){
            super(ItemView);
            company_name = (TextView) itemView.findViewById(R.id.company_name_TV);
            tariff = (TextView) itemView.findViewById(R.id.tariff_TV);
            duration = (TextView) itemView.findViewById(R.id.duration_TV);
            make_order = (Button) itemView.findViewById(R.id.make_order_BTN);
        }
    }

    private List<Companies> items;
    private Context context;
    private Client client;
    private SharedPreferences sharedPreferences;

    public CompaniesAdapter(Context context, List<Companies> items){
        this.items = items;
        this.context = context;
        client = Client.getInstance();
        sharedPreferences = context.getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.companies_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Companies company = items.get(position);
        holder.company_name.setText(company.name);
        holder.tariff.setText(String.valueOf(company.tariff));
        holder.duration.setText(company.closestDuration);
        holder.make_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.setCompany_id(company.id);
                client.setWorker_id(company.closestWorkerId);
                String api_key = sharedPreferences.getString("api_key", "");
                CreateOrderAsync createOrderAsync = new CreateOrderAsync(context, api_key, new CreateOrderCallBack() {
                    @Override
                    public void created(boolean result, Worker worker) {
                        if(result){
                            Log.d("ORDER", String.valueOf(worker.order_id));
                            client.setOrder_id(worker.order_id);
                            Intent intent = new Intent(context, ConfirmOrderService.class);
                            context.startService(intent);
                        }
                        else {
                            Log.d("ORDER", "Заказ НЕ сформирован");
                        }
                    }
                });
                createOrderAsync.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
