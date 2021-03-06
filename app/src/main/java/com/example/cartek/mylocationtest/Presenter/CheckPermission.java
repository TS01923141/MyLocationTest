package com.example.cartek.mylocationtest.Presenter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.cartek.mylocationtest.View.IMapsActivity;
import com.example.cartek.mylocationtest.View.MapsActivity;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by CarTek on 2017/12/29.
 */

public class CheckPermission implements ICheckPermission {
    IMapsActivity mapsActivity;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    public CheckPermission(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
    }

    /**
     * 檢查/取得權限
     */
    public boolean getLocationPermission() {
        if(ContextCompat.checkSelfPermission(mapsActivity.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            ActivityCompat.requestPermissions((Activity) mapsActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
        return false;
    }

    /**
     * 使用者按下允許/拒絕權限要求後反應
     */
    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return true;
                }
            }
        }
        return false;
    }
}
