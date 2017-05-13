package com.example.edriver.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.edriver.Activity.NavigationDrawerActivity;
import com.example.edriver.Activity.OrderInfoActivity;
import com.example.edriver.Model.OrderInfo;
import com.example.edriver.R;
import com.example.edriver.Service.GetOrderService;
import com.example.edriver.Utils.App;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
        android.location.LocationListener, RoutingListener, SensorEventListener {

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
    private SharedPreferences sharedPreferences;
    private float zoom;
    private LatLng N;
    private float mDeclination;
    private float[] mRotationMatrix = new float[16];
    private SensorManager mSensorManager;

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
                Toast.makeText(getContext(), "Телефон не поддерживает Google Play Services!", Toast.LENGTH_LONG).show();
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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private Location getMyLocation() {
        checkPermission();
        if (googleApiClient.isConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        return null;
    }

    private LatLng getVector(LatLng start, LatLng end){
        return new LatLng(end.latitude-start.latitude, end.longitude - start.longitude);
    }

    private double getVectorLength(LatLng vector){
        return Math.sqrt((vector.latitude*vector.latitude) + (vector.longitude*vector.longitude));
    }

    private double scalarMultiplication(LatLng a, LatLng b){
        return a.latitude*b.latitude + a.longitude*b.longitude;
    }

    private float getAngle(LatLng a, LatLng b){
        double cos = scalarMultiplication(a, b)/(getVectorLength(a)*getVectorLength(b));
        Log.d("COS", String.valueOf(cos));
        Log.d("ACOS", String.valueOf(Math.acos(cos)));
        float result = 360.0f - (float)Math.acos(cos);
        Log.d("ANGLE", String.valueOf(result));
        return result;
    }

    private void moveCameraToMyLocation(boolean flag){
        if(myLocation!=null){
            if(map!=null){
                isLocated = true;
                map.clear();
                if(flag){
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                            .zoom(zoom)
                            .bearing(360.0f)
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    map.animateCamera(cameraUpdate);
                }
                map.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
            }
            else {
                Toast.makeText(getContext(), "Карта не может отобразить Ваше местоположение!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(), "Не могу определить местоположение. Попробуйте еще раз!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
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
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void DrawRoute(boolean flag){
        if(map!=null){
            map.clear();
            if(flag){
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(new LatLngBounds(new LatLng(myLocation.getLatitude(),
//                        myLocation.getLongitude()), new LatLng(order.getLatitude(), order.getLongitude())), 50);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        //.target(new LatLng(order.getLatitude(), order.getLongitude()))
                        .zoom(zoom)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
            }
            else{
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .zoom(zoom)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
            }

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
            Toast.makeText(getContext(), "Карта не может отобразить Ваше местоположение!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        polylines = new ArrayList<>();
        zoom = 15;
        N = new LatLng(0.0f, 0.00000001f);
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        } else {
            Toast.makeText(getContext(), "Google Play Services не поддерживаются данным устройством!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_map_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        find_me_BTN = (ImageButton) view.findViewById(R.id.find_me_BTN);
        find_me_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order.getOrder_status() == Order.OnTheWay){
                    DrawRoute(true);
                }
                else {
                    Location temp = getMyLocation();
                    myLocation.setLatitude(temp.getLatitude());
                    myLocation.setLongitude(temp.getLongitude());
                    moveCameraToMyLocation(true);
                }
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                zoom = map.getCameraPosition().zoom;
            }
        });
        if(order.getOrder_status()==Order.OnTheWay){
            DrawRoute(true);
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
        GeomagneticField field = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis()
        );

        // getDeclination returns degrees
        mDeclination = field.getDeclination();
        Log.d("DECL", String.valueOf(mDeclination));

        if((location.getLatitude() == myLocation.getLatitude()) && (location.getLongitude() == myLocation.getLongitude())){
            myLocation.setNew(false);
        }
        else {
            myLocation.setLatitude(location.getLatitude());
            myLocation.setLongitude(location.getLongitude());
            myLocation.setNew(true);
        }
        if(order.getOrder_status()==Order.OnTheWay){
            DrawRoute(false);
            return;
        }
        if(order.getOrder_status()==Order.Performing){
            moveCameraToMyLocation(true);
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
        intentFilter.addAction(MyAction.DrawTwoMarks);
        intentFilter.addAction(MyAction.OrderCompleted);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
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
                moveCameraToMyLocation(true);
                PerformingFragment performingFragment = new PerformingFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.info_container_fragment, performingFragment).commit();
                break;
            }
            case Order.CanceledByClient:{
                moveCameraToMyLocation(true);
                Intent intent_order = new Intent(getActivity(), GetOrderService.class);
                getContext().startService(intent_order);
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
                    DrawRoute(true);
                    break;
                }
                case MyAction.StartedImplementation:{
                    moveCameraToMyLocation(true);
                    break;
                }
                case MyAction.OrderCompleted:{
                    sharedPreferences = getContext().getSharedPreferences("API_KEY",Context.MODE_PRIVATE);
                    String api_key = sharedPreferences.getString("api_key", "");
                    App.getApi().getOrderInfo(api_key, order.getOrder_id()).enqueue(new Callback<OrderInfo>() {
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
                                    default:{
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
                default:{
                    break;
                }
            }
        }
    };

    private void updateCamera(float bearing) {
        CameraPosition oldPos = map.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float bearing = (float)Math.toDegrees(orientation[0]) + mDeclination;
            Log.d("BEARING", String.valueOf(bearing));
            updateCamera(bearing);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
