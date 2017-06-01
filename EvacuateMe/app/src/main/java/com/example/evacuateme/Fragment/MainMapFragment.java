package com.example.evacuateme.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.evacuateme.Activity.NavigationDrawerActivity;
import com.example.evacuateme.Activity.OrderInfoActivity;
import com.example.evacuateme.Model.OrderInfo;
import com.example.evacuateme.R;
import com.example.evacuateme.Service.CheckOrderStatusService;
import com.example.evacuateme.Service.GetWorkerLocationService;
import com.example.evacuateme.Utils.App;
import com.example.evacuateme.Utils.Client;
import com.example.evacuateme.Utils.MyAction;
import com.example.evacuateme.Utils.STATUS;
import com.example.evacuateme.Utils.Worker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, android.location.LocationListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 0; // 10 sec
    private static int FATEST_INTERVAL = 0; // 5 sec
    private static int DISPLACEMENT = 0; // 10 meters

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private boolean isLocated;
    private ImageButton find_me_BTN;
    private FragmentTransaction fragmentTransaction;
    private Client client;
    private Worker worker;
    private SharedPreferences sharedPreferences;
    private float zoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = Client.getInstance();
        worker = Worker.getInstance();
        zoom = 15;
        getActivity().setTitle("EvacuateMe");
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        } else {
            Toast.makeText(getContext(), "Google Play Services не поддерживаются данным устройством!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_map_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        find_me_BTN = (ImageButton) view.findViewById(R.id.find_me_BTN);
        find_me_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (worker.getOrder_status()){
                    case STATUS.OnTheWay:{
                        showWorkerPosition(true);
                        break;
                    }
                    case STATUS.Performing:{
                        showOrderLocation(true);
                        break;
                    }
                    default:{
                        Location temp = getMyLocation();
                        client.setLatitude(temp.getLatitude());
                        client.setLongitude(temp.getLongitude());
                        moveCameraToMyLocation();
                        break;
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                zoom = map.getCameraPosition().zoom;
            }
        });

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if(worker.getOrder_status()==STATUS.OnTheWay){
                    showWorkerPosition(true);
                }
            }
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getContext(), "Телефон не поддерживает Google Play Services!",
                        Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        checkPermission();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    private Location getMyLocation() {
        checkPermission();
        if (googleApiClient.isConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        return null;
    }

    //работа с картой

    private void moveCameraToMyLocation(){
        if(map!=null){
            isLocated = true;
            map.clear();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(client.getLatitude(), client.getLongitude()))
                    .zoom(zoom)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate, 300, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });
            map.addMarker(new MarkerOptions().position(new LatLng(client.getLatitude(), client.getLongitude())));
        }
        else {
            Toast.makeText(getContext(), "Карта не может отобразить Ваше местоположение!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWorkerPosition(boolean flag){
        if(map!=null){
            map.clear();
            if(flag){
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(client.getLatitude(), client.getLongitude()))
                        .include(new LatLng(worker.getLatitude(), worker.getLongitude()));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 200);
                map.addMarker(new MarkerOptions().position(new LatLng(client.getLatitude(),
                        client.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                map.addMarker(new MarkerOptions().position(new LatLng(worker.getLatitude(),
                        worker.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                map.moveCamera(cameraUpdate);
            }
            else {
                map.addMarker(new MarkerOptions().position(new LatLng(client.getLatitude(),
                        client.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                map.addMarker(new MarkerOptions().position(new LatLng(worker.getLatitude(),
                        worker.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        }
        else {
            Toast.makeText(getContext(), "Карта не может отобразить Ваше местоположение!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrderLocation(boolean flag){
        if(map!=null){
            map.clear();
            if(flag){
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(worker.getLatitude(), worker.getLongitude()))
                        .zoom(zoom)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
            }
            map.addMarker(new MarkerOptions().position(new LatLng(worker.getLatitude(),
                    worker.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        else {
            Toast.makeText(getContext(), "Карта не может отобразить Ваше местоположение!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Установить GPS соединение не удалось!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(isLocated){
            return;
        }
        if(worker.getOrder_status() == STATUS.OnTheWay || worker.getOrder_status() == STATUS.Performing){
            return;
        }
        client.setLatitude(location.getLatitude());
        client.setLongitude(location.getLongitude());
        moveCameraToMyLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyAction.OrderCanceledByClient);
        intentFilter.addAction(MyAction.WorkerLocationChanged);
        intentFilter.addAction(MyAction.OrderConfirmed);
        intentFilter.addAction(MyAction.OrderLocationChanged);
        intentFilter.addAction(MyAction.OrderPerforming);
        intentFilter.addAction(MyAction.OrderCompleted);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
        if (checkPlayServices() && googleApiClient.isConnected()) {
            startLocationUpdates();
        }
        switch (worker.getOrder_status()){
            case STATUS.OnTheWay:{
                OnTheWayFragment onTheWayFragment = new OnTheWayFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.info_container_fragment, onTheWayFragment).commit();
                break;
            }

            case STATUS.Performing:{
                PerformingFragment performingFragment = new PerformingFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.info_container_fragment, performingFragment).commit();
                break;
            }

            default:{
                StartFragment startFragment = new StartFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.info_container_fragment, startFragment).commit();
                break;
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            switch (intent.getAction()){
                case MyAction.OrderCanceledByClient:{
                    Intent service_intent = new Intent(getContext(), GetWorkerLocationService.class);
                    getContext().stopService(service_intent);
                    Intent status_service = new Intent(getContext(), CheckOrderStatusService.class);
                    getContext().stopService(status_service);
                    StartFragment startFragment = new StartFragment();
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.info_container_fragment, startFragment).commit();
                    break;
                }
                case MyAction.WorkerLocationChanged:{
                    showWorkerPosition(false);
                    break;
                }
                case MyAction.OrderPerforming:{
                    showOrderLocation(true);
                    Toast.makeText(getContext(), "Водитель приехал!", Toast.LENGTH_SHORT);
                    PerformingFragment performingFragment = new PerformingFragment();
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.info_container_fragment, performingFragment).commit();
                    break;
                }
                case MyAction.OrderLocationChanged:{
                    showOrderLocation(true);
                    break;
                }
                case MyAction.OrderCompleted:{
                    Toast.makeText(getContext(), "Заказ завершен!", Toast.LENGTH_SHORT);
                    Intent service_intent = new Intent(getContext(), GetWorkerLocationService.class);
                    getContext().stopService(service_intent);
                    Intent status_service = new Intent(getContext(), CheckOrderStatusService.class);
                    getContext().stopService(status_service);
                    sharedPreferences = getContext().getSharedPreferences("API_KEY",Context.MODE_PRIVATE);
                    String api_key = sharedPreferences.getString("api_key", "");
                    App.getApi().getOrderInfo(api_key, worker.getOrder_id()).enqueue(new Callback<OrderInfo>() {
                        @Override
                        public void onResponse(Call<OrderInfo> call, Response<OrderInfo> response) {
                            if(response==null){
                                Toast.makeText(getContext(), "Получить информацию о заказе не удалось! Свяжитесь с водитилем!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), NavigationDrawerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                getContext().startActivity(intent);
                            }
                            else {
                                switch (response.code()){
                                    case STATUS.Ok:{
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("order_id", response.body().order_id);
                                        bundle.putString("company", response.body().company);
                                        bundle.putDouble("distance", response.body().distance);
                                        bundle.putDouble("summary", response.body().summary);
                                        Intent activity_intent = new Intent(getContext(), OrderInfoActivity.class);
                                        activity_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity_intent.putExtra("data", bundle);
                                        getContext().startActivity(activity_intent);
                                        break;
                                    }
                                    case STATUS.BadRequest:{
                                        break;
                                    }
                                    case STATUS.Unauthorized:{
                                        break;
                                    }
                                    case STATUS.NotFound:{
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderInfo> call, Throwable t) {

                        }
                    });
                    break;
                }
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
