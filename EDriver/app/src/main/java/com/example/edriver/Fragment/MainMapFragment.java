package com.example.edriver.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.edriver.R;
import com.example.edriver.Service.GetOrderService;
import com.example.edriver.Utils.MyAction;
import com.example.edriver.Utils.MyLocation;
import com.example.edriver.Utils.Order;
import com.example.edriver.Utils.STATUS;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, android.location.LocationListener, RoutingListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 10; // 10 sec
    private static int FATEST_INTERVAL = 5; // 5 sec
    private static int DISPLACEMENT = 5; // 10 meters

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private ImageButton find_me_BTN;
    private boolean isLocated;
    private FragmentTransaction fragmentTransaction;
    private Order order = Order.getInstance();
    private MyLocation myLocation = MyLocation.getInstance();
    private List<Polyline> polylines;

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

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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

    private void moveCameraToMyLocation(boolean flag){
        if(myLocation!=null){
            if(map!=null){
                isLocated = true;
                map.clear();
                if(flag){
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                            .zoom(15)
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    map.moveCamera(cameraUpdate);
                }
                map.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
            }
            else {
                Toast.makeText(getContext(), "Карта не может отобразить Ваше местоположение!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(), "Не могу определить местоположение. Попробуйте еще раз!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        for (int i = 0; i <route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.CYAN);
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
        }
        Log.d("DISTANCE", route.get(route.size()-1).getDistanceText());
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void DrawRoute(){
        if(map!=null){
            map.clear();
            double mLat = (order.getLatitude() + myLocation.getLatitude())/2;
            double mLong = (order.getLongitude() + myLocation.getLongitude())/2;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLat, mLong))
                    .zoom(13)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
            map.addMarker(new MarkerOptions().position(new LatLng(order.getLatitude(),
                    order.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            map.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(),
                    myLocation.getLongitude()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            LatLng start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng end = new LatLng(order.getLatitude(), order.getLongitude());
            Routing routing = new Routing.Builder().travelMode(Routing.TravelMode.DRIVING).withListener(this)
                    .waypoints(start, end).build();
            routing.execute();
        }
        else {
            Toast.makeText(getContext(), "Карта не может отобразить Ваше местоположение!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        polylines = new ArrayList<>();
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
//                myLocation.setLatitude(47.30148265);
//                myLocation.setLongitude(39.72292542);
//                myLocation.setNew(true);
//                moveCameraToMyLocation();

//                //работает
                Location temp = getMyLocation();
                myLocation.setLatitude(temp.getLatitude());
                myLocation.setLongitude(temp.getLongitude());
                moveCameraToMyLocation(true);
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        switch (order.getOrder_status()){

            case Order.OnTheWay:{
                DrawRoute();
                break;
            }

            default:{
                break;
            }
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

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Установить GPS соединение не удалось!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
//        myLocation.setLatitude(47.30148265);
//        myLocation.setLongitude(39.72292542);
//        myLocation.setNew(true);
//
//        if(order.getOrder_status()==Order.OnTheWay){
//            DrawToMarks();
//            return;
//        }
//
//        if(isLocated){
//            return;
//        }
//        else {
//            moveCameraToMyLocation();
//        }

        //вроде работает
        if((location.getLatitude() == myLocation.getLatitude()) && (location.getLongitude() == myLocation.getLongitude())){
            myLocation.setNew(false);
        }
        else {
            myLocation.setLatitude(location.getLatitude());
            myLocation.setLongitude(location.getLongitude());
            myLocation.setNew(true);
        }


        if(order.getOrder_status()==Order.OnTheWay){
            DrawRoute();
            return;
        }

        if(order.getOrder_status()==Order.Performing){
            moveCameraToMyLocation(false);
            return;
        }

        if(isLocated){
            return;
        }
        else {
            myLocation.setLatitude(location.getLatitude());
            myLocation.setLongitude(location.getLongitude());
            moveCameraToMyLocation(true);
        }
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
        if (checkPlayServices() && googleApiClient.isConnected()) {
            startLocationUpdates();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyAction.Order);
        intentFilter.addAction(MyAction.OrderCanceledByClient);
        intentFilter.addAction(MyAction.StartedImplementation);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
        Log.d("ORDER_STATUS", String.valueOf(order.getOrder_status()));
        switch (order.getOrder_status()){
            case Order.Awaiting:{
                SelectionFragment selectionFragment = new SelectionFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.info_container_fragment, selectionFragment).commit();
                break;
            }

            case Order.OnTheWay:{
                OnTheWayFragment onTheWayFragment = new OnTheWayFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.info_container_fragment, onTheWayFragment).commit();
                break;
            }

            case Order.Performing:{
                PerformingFragment performingFragment = new PerformingFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.info_container_fragment, performingFragment).commit();
                break;
            }

            case Order.CanceledByClient:{
                moveCameraToMyLocation(true);
                Intent intent_order = new Intent(getActivity(), GetOrderService.class);
                getContext().startService(intent_order);
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
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
               case MyAction.Order:{
                   SelectionFragment selectionFragment = new SelectionFragment();
                   fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                   fragmentTransaction.replace(R.id.info_container_fragment, selectionFragment).commit();
                   break;
                }

                case MyAction.OrderCanceledByClient:{
                    Toast.makeText(context, "Заказ бы отменен клиентом!", Toast.LENGTH_SHORT).show();
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    StartFragment startFragment = new StartFragment();
                    fragmentTransaction.replace(R.id.info_container_fragment, startFragment).commit();
                    Intent new_intent = new Intent(context, GetOrderService.class);
                    getActivity().startService(new_intent);
                    moveCameraToMyLocation(true);
                    break;
                }

                case MyAction.DrawTwoMarks:{
                    DrawRoute();
                    break;
                }

                case MyAction.StartedImplementation:{
                    moveCameraToMyLocation(true);
                    break;
                }

                default:{
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
