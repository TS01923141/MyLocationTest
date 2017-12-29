package com.example.cartek.mylocationtest.Presenter;

import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.cartek.mylocationtest.View.IMapsActivity;
import com.example.cartek.mylocationtest.View.MapsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by CarTek on 2017/12/29.
 */

public class GetMyLocation implements IGetMyLocation {
    IMapsActivity mapsActivity;
    ICheckPermission checkPermission;
    private GoogleMap mMap;
    private static final int DEAFULT_ZOOM = 15;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public GetMyLocation(MapsActivity mapsActivity, FusedLocationProviderClient mFusedLocationProviderClient){
        this.mapsActivity = mapsActivity;
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
        this.checkPermission = mapsActivity.getCheckPermission();
    }
    @Override
    public void initMap(GoogleMap googleMap){
        this.mMap = googleMap;
    }

    /**
     * 取得裝置地點
     */
    @Override
    public void getDeviceLocation(){
        try{
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) mapsActivity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEAFULT_ZOOM));
                        }else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEAFULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
        } catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * 依使用者是否有給予權限決定更新/要求權限
     */
    @Override
    public void updateLocationUI(boolean mLocationPermissionGranted) {
        if(mMap == null){
            return;
        }
        try{
            if(mLocationPermissionGranted){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            }else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                checkPermission.getLocationPermission();
            }
        }catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
