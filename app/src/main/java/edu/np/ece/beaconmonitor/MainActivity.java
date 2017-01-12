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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zqi2 on 17/12/16.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 123;
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";
    private static final String REGION_NAME = "TestBeacon";
    private static final String REGION_UUID = "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6";
    Intent mServiceIntent;
    private Context context;
    private BeaconMonitoringService mSensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

//        mSensorService = new BeaconMonitoringService();
//        mServiceIntent = new Intent(context, mSensorService.getClass());
//        if (!isMyServiceRunning(mSensorService.getClass())) {
//            startService(mServiceIntent);
//        }
//
//        checkPermissions();
        // Check for required permissions
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {
                    // Add permission here
                    // No need to add RECEIVE_BOOT_COMPLETED because it is normal permission
                    // https://unionassets.com/android-native-plugin/runtime-permissions-511
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }

        // Check phone manufacturer
        // If it is XiaoMi phone, guide user to manually grant Auto Start permission
        Log.i(TAG, "Manufacturer = " + Build.MANUFACTURER);
        Log.i(TAG, "Model = " + Build.MODEL);
        ((TextView) findViewById(R.id.tvModel)).setText(Build.MANUFACTURER + " - " + Build.MODEL);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start service upon running of MainActivity
        Intent intent = new Intent(this, BeaconMonitoringService.class);
        this.startService(intent);

        // Check if service is running in background
        Boolean isServiceRunning = isServiceRunning(
                MainActivity.this.getApplicationContext(),
                BeaconMonitoringService.class);
        ((TextView) findViewById(R.id.tvStatus)).setText("Service is " + (isServiceRunning ? "running" : "NOT running"));
    }

    @Override
    protected void onDestroy() {
//        beaconManager.unbind(this);
        //-- Stop service so that it will restart
        stopService(mServiceIntent);
        super.onDestroy();
    }

//    private void checkPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // Android M Permission check?
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("This app needs location access");
//                builder.setMessage("Please grant location access so this app can detect beacons.");
//                builder.setPositiveButton(android.R.string.ok, null);
//                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//                    }
//                });
//                builder.show();
//            }
//        }
//    }

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

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isServiceRunning(Context context, Class<?> serviceClass) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            Log.d(TAG, String.format("Service:%s", runningServiceInfo.service.getClassName()));
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
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
