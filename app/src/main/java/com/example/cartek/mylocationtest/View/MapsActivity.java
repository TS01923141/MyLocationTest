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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IMapsActivity {

    ICheckPermission checkPermission;
    IGetMyLocation getMyLocation;
    private GoogleMap mMap;

    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    public Context getContext(){
        return this;
    }
    @Override
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
        getMyLocation = new GetMyLocation(this, mFusedLocationProviderClient);
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

        if(mLocationPermissionGranted){
            getMyLocation.updateLocationUI(mLocationPermissionGranted);
        }
    }

    /**
     * 使用者按下允許/拒絕權限要求後反應
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = checkPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getMyLocation.updateLocationUI(mLocationPermissionGranted);
    }
}
