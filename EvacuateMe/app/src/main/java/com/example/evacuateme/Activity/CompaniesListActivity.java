package com.example.evacuateme.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.evacuateme.Adapter.CompaniesAdapter;
import com.example.evacuateme.Model.Companies;
import com.example.evacuateme.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CompaniesListActivity extends AppCompatActivity {
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView companies_RV;
    private CompaniesAdapter companiesAdapter;
    private List<Companies> list_copmanies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list);
        Intent intent = getIntent();
        if(intent!=null){
            companies_RV = (RecyclerView) findViewById(R.id.companies_RV);
            linearLayoutManager = new LinearLayoutManager(this);
            companies_RV.setLayoutManager(linearLayoutManager);
            String temp = intent.getStringExtra("list_companies");
            Type type = new TypeToken<List<Companies>>(){}.getType();
            list_copmanies = new Gson().fromJson(temp, type);
            companiesAdapter = new CompaniesAdapter(CompaniesListActivity.this, list_copmanies);
            companies_RV.setAdapter(companiesAdapter);
        }
    }

}
