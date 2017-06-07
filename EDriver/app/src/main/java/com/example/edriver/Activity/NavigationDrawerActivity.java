package com.example.edriver.Activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.edriver.AsyncTask.GetOrderHistoryAsync;
import com.example.edriver.Fragment.GpsOffFragment;
import com.example.edriver.Fragment.HistoryFragment;
import com.example.edriver.Fragment.MainMapFragment;
import com.example.edriver.Interface.GetOrderHistoryCallBack;
import com.example.edriver.Model.OrderHistory;
import com.example.edriver.R;
import com.example.edriver.Utils.Gps;
import com.example.edriver.Utils.Net;
import com.google.gson.Gson;

import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentTransaction fragmentTransaction;
    private SharedPreferences sharedPreferences;
    private boolean isMapAttached = false;
    public static boolean active = false;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        changeUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_map:{
                changeUI();
                break;
            }
            case R.id.nav_orders:{
                isMapAttached = false;
                sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
                String api_key = sharedPreferences.getString("api_key", "");
                GetOrderHistoryAsync getOrderHistoryAsync = new GetOrderHistoryAsync(NavigationDrawerActivity.this, api_key,
                        new GetOrderHistoryCallBack() {
                            @Override
                            public void completed(boolean result, List<OrderHistory> list_orders) {
                                if(result){
                                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    HistoryFragment historyFragment = new HistoryFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("list_orders", new Gson().toJson(list_orders));
                                    historyFragment.setArguments(bundle);
                                    fragmentTransaction.replace(R.id.main_container_fragment, historyFragment).commit();
                                }
                            }
                        });
                getOrderHistoryAsync.execute();
                break;
            }
            case R.id.nav_exit:{
                exit();
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeUI(){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(isMapAttached){
            return;
        }
        if(Net.isAvailable(NavigationDrawerActivity.this) && Gps.isAvailable(NavigationDrawerActivity.this)){
            MainMapFragment mainMapFragment = new MainMapFragment();
            fragmentTransaction.replace(R.id.main_container_fragment, mainMapFragment);
            isMapAttached = true;
        }
        else {
            GpsOffFragment gpsOffFragment = new GpsOffFragment();
            fragmentTransaction.replace(R.id.main_container_fragment, gpsOffFragment);
        }
        fragmentTransaction.commit();
    }

    private void exit(){
        sharedPreferences = getSharedPreferences("IS_LOGIN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_isLogin = sharedPreferences.edit();
        editor_isLogin.putBoolean("is_login", false);
        editor_isLogin.apply();
        sharedPreferences = getSharedPreferences("API_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_api_key = sharedPreferences.edit();
        editor_api_key.putString("api_key", "");
        editor_api_key.apply();
        Intent intent = new Intent(NavigationDrawerActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
