package com.example.cartek.mylocationtest.Presenter;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by CarTek on 2017/12/29.
 */

public interface IGetMyLocation {
    void initMap(GoogleMap googleMap);

    void getDeviceLocation();


    void updateLocationUI(boolean mLocationPermissionGranted);
}
