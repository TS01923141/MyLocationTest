package com.example.cartek.mylocationtest.Presenter;

import android.support.annotation.NonNull;

/**
 * Created by CarTek on 2017/12/29.
 */

public interface ICheckPermission {
    boolean getLocationPermission();

    boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
