package edu.np.ece.beaconmonitor;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by zqi2 on 17/12/16.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
//    private BeaconManager beaconManager;

    Intent mServiceIntent;
    private BeaconConsumingService mSensorService;

    private static final String REGION_NAME = "TestBeacon";
    private static final String REGION_UUID = "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        mSensorService = new BeaconConsumingService();
        mServiceIntent = new Intent(context, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

//        beaconManager = BeaconManager.getInstanceForApplication(this);
//        beaconManager.bind(this);
        checkPermissions();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.getIntent() != null) {
            String message = this.getIntent().getStringExtra("message");
            if (message != null) {
                TextView textView = (TextView) this.findViewById(R.id.textView);
                textView.setText(message);
            }
        }
    }

    @Override
    protected void onDestroy() {
//        beaconManager.unbind(this);
        //-- Stop service so that it will restart
        stopService(mServiceIntent);
        super.onDestroy();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check?
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

//    @Override
//    public void onBeaconServiceConnect() {
//        Log.d(TAG, "onBeaconServiceConnect()");
//
//        try {
//            beaconManager.stopMonitoringBeaconsInRegion(new Region("AnyBeacon", null, null, null));
//        } catch (RemoteException e) {
//        }
//
//        beaconManager.addMonitorNotifier(new MonitorNotifier() {
//
//            @Override
//            public void didEnterRegion(Region region) {
//                Log.i(TAG, "didEnterRegion(): " + region.getUniqueId());
//            }
//
//            @Override
//            public void didExitRegion(Region region) {
//                Log.i(TAG, "didExitRegion(): " + region.getUniqueId());
//            }
//
//            @Override
//            public void didDetermineStateForRegion(int state, Region region) {
//                Log.d(TAG, "didDetermineStateForRegion(): " + String.valueOf(state) + " " + region.getUniqueId());
//            }
//        });
//
////        try {
////            beaconManager.startMonitoringBeaconsInRegion(
////                    new Region(REGION_NAME, Identifier.parse(REGION_UUID), null, null));
////        } catch (RemoteException e) {
////        }
//    }
}
