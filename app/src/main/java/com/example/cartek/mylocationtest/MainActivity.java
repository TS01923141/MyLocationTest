package com.example.cartek.mylocationtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

//not use
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button btnGetLastLocation;
    TextView textLastLocation;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;

    private boolean getService = false;     //是否已開啟定位服務

    private LocationManager status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGetLastLocation = (Button) findViewById(R.id.getlastlocation);
        btnGetLastLocation.setOnClickListener(btnGetLastLocationOnClickListener);
        textLastLocation = (TextView) findViewById(R.id.lastlocation);

        status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //如果沒有授權使用定位就會跳出來這個
            // TODO: Consider calling

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            requestLocationPermission(); // 詢問使用者開啟權限
            return;
        }



    }

    private void requestLocationPermission(){
        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 取得授權狀態，參數是請求授權的名稱
            int hasPermission = checkSelfPermission(
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasPermission2 = checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION);


            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                //     第一個參數是請求授權的名稱
                //     第二個參數是請求代碼
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }
//            if (hasPermission2!=PackageManager.PERMISSION_GRANTED){
//                requestPermissions(
//                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                        REQUEST_COARSE_LOCATION_PERMISSION);
//
//            }
            else {
                // 啟動地圖與定位元件

            }
        }
    }

    View.OnClickListener btnGetLastLocationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)&& status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
                if(mGoogleApiClient != null){
                    if(mGoogleApiClient.isConnected()){
                        getMyLocation();
                    }else{
                        Toast.makeText(MainActivity.this,
                                "!mGoogleApiClient.isConnected()", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,
                            "mGoogleApiClient == null", Toast.LENGTH_LONG).show();
                }


            } else {
                Toast.makeText(MainActivity.this,"請開啟定位",Toast.LENGTH_LONG).show();
                getService = true; //確認開啟定位服務
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
            }


        }
    };

    private void getMyLocation(){
        try{
            /* code should explicitly check to see if permission is available
            (with 'checkPermission') or explicitly handle a potential 'SecurityException'
             */
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                textLastLocation.setText(
                        String.valueOf(mLastLocation.getLatitude()) + "\n"
                                + String.valueOf(mLastLocation.getLongitude()));
                Toast.makeText(MainActivity.this,
                        String.valueOf(mLastLocation.getLatitude()) + "\n"
                                + String.valueOf(mLastLocation.getLongitude()),
                        Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this,
                        "mLastLocation == null",
                        Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e){
            Toast.makeText(MainActivity.this,
                    "SecurityException:\n" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this,
                "onConnectionSuspended: " + String.valueOf(i),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
    }
}
