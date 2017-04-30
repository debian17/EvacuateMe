package com.example.evacuateme.Adapter;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evacuateme.Activity.NavigationDrawerActivity;
import com.example.evacuateme.AsyncTask.ChangeOrderStatusAsync;
import com.example.evacuateme.AsyncTask.CreateOrderAsync;
import com.example.evacuateme.Interface.ChangeOrderStatusCallBack;
import com.example.evacuateme.Interface.CreateOrderCallBack;
import com.example.evacuateme.Model.Companies;
import com.example.evacuateme.Model.OrderData;
import com.example.evacuateme.Model.OrderStatus;
import com.example.evacuateme.R;
import com.example.evacuateme.Service.ConfirmOrderService;
import com.example.evacuateme.Utils.Client;
import com.example.evacuateme.Utils.MyAction;
import com.example.evacuateme.Utils.STATUS;
import com.example.evacuateme.Utils.Worker;

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
    private Worker worker;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    public CompaniesAdapter(Context context, List<Companies> items){
        this.items = items;
        this.context = context;
        client = Client.getInstance();
        worker = Worker.getInstance();
        sharedPreferences = context.getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyAction.OrderConfirmed);
        intentFilter.addAction(MyAction.OrderCanceledByWorker);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Ожидайте подтверждения заказа...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
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
                worker.setCompany_id(company.id);
                worker.setWorker_id(company.closestWorkerId);
                String api_key = sharedPreferences.getString("api_key", "");
                CreateOrderAsync createOrderAsync = new CreateOrderAsync(context, api_key, new CreateOrderCallBack() {
                    @Override
                    public void created(boolean result, OrderData orderData) {
                        if(result){
                            worker.setOrder_id(orderData.order_id);
                            worker.setLatitude(orderData.latitude);
                            worker.setLongitude(orderData.longitude);
                            worker.setPhone(orderData.phone);
                            final Intent intent = new Intent(context, ConfirmOrderService.class);
                            progressDialog.show();
                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    context.stopService(intent);
                                    ChangeOrderStatusAsync changeOrderStatusAsync = new ChangeOrderStatusAsync(context,
                                            worker.getOrder_id(), STATUS.CanceledByClient, new ChangeOrderStatusCallBack() {
                                        @Override
                                        public void completed(boolean result) {
                                            if(result){
                                                Toast.makeText(context, "Вы отменили заказ!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    changeOrderStatusAsync.execute();
                                }
                            });
                            context.startService(intent);
                        }
                        else {
                            Log.d("ORDER", "Заказ НЕ сформирован!");
                        }
                    }
                });
                createOrderAsync.execute();
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.dismiss();

            switch (intent.getAction()){
                case MyAction.OrderConfirmed:{
                    Intent new_intent = new Intent(context, NavigationDrawerActivity.class);
                    new_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(new_intent);
                    break;
                }
                case MyAction.OrderCanceledByWorker:{
                    Toast.makeText(context, "Работник отменил заказ!", Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    Log.d("НЕВЕДОМАЯ", "ДИЧЬ");
                    break;
            }
        }
    };

    @Override
    public int getItemCount() {
        return items.size();
    }
}
