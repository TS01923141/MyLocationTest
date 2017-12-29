package com.example.cartek.mylocationtest.View;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.cartek.mylocationtest.Presenter.CheckPermission;
import com.example.cartek.mylocationtest.Presenter.GetMyLocation;
import com.example.cartek.mylocationtest.Presenter.ICheckPermission;
import com.example.cartek.mylocationtest.Presenter.IGetMyLocation;
import com.example.cartek.mylocationtest.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ICheckPermission checkPermission;
    IGetMyLocation getMyLocation;

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    private boolean mLocationPermissionGranted;
    private static final int DEAFULT_ZOOM = 15;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public Context getContext(){
        return this;
    }
    public ICheckPermission getCheckPermission(){
        return checkPermission;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.cartek.mylocationtest.R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermission = new CheckPermission(this);
        getMyLocation = new GetMyLocation(this, mLastKnownLocation, mFusedLocationProviderClient);
    }

    /**
     *
     *Manipulates when MapReady
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mLocationPermissionGranted = checkPermission.getLocationPermission();
        getMyLocation.initMap(mMap);

//        Log.i("onMapReady","mLocationPermissionGranted: "+ String.valueOf(mLocationPermissionGranted));
        if(mLocationPermissionGranted){
//            Log.i("onMapReady.true","mLocationPermissionGranted: "+ String.valueOf(mLocationPermissionGranted));
            getMyLocation.updateLocationUI(mLocationPermissionGranted);
        }
//        getLocationPermission();
    }

    /**
     * 檢查/取得權限
     */
    public void getLocationPermission() {
        Log.i("getLocationPermission","mLocationPermissionGranted: "+ String.valueOf(mLocationPermissionGranted));
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
            updateLocationUI();
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * 使用者按下允許/拒絕權限要求後反應
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = checkPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.i("onRequestPermissionsResult","mLocationPermissionGranted: "+ String.valueOf(mLocationPermissionGranted));
//        mLocationPermissionGranted = false;
//        switch (requestCode){
//            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:{
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    mLocationPermissionGranted = true;
//                }
//            }
//        }
        getMyLocation.updateLocationUI(mLocationPermissionGranted);
//        updateLocationUI();
    }

    /**
     * 依使用者是否有給予權限決定更新/要求權限
     */
    private void updateLocationUI() {
        if(mMap == null){
            return;
        }
        Log.i("updateLocationUI","mLocationPermissionGranted: "+ String.valueOf(mLocationPermissionGranted));
        try{
            if(mLocationPermissionGranted){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            }else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                Log.i("updateLocationUI.false","mLocationPermissionGranted: "+ String.valueOf(mLocationPermissionGranted));
                getLocationPermission();
            }
        }catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * 取得裝置地點
     */
    private void getDeviceLocation(){
        try{
            Log.i("getDeviceLocation", "mLocationPermissionGranted"+ String.valueOf(mLocationPermissionGranted));
            if(mLocationPermissionGranted){
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEAFULT_ZOOM));
                        }else {
                            Log.d(TAG,"Current location is null. Using defaults");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEAFULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
