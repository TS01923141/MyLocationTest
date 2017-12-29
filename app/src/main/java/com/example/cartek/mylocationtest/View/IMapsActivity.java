package com.example.cartek.mylocationtest.View;

import android.content.Context;

import com.example.cartek.mylocationtest.Presenter.ICheckPermission;

/**
 * Created by CarTek on 2017/12/29.
 */

public interface IMapsActivity {
    Context getContext();

    ICheckPermission getCheckPermission();
}
