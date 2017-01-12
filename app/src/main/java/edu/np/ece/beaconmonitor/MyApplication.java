package edu.np.ece.beaconmonitor;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;

/**
 * Created by zqi2 on 17/12/16.
 */

public class MyApplication extends Application implements BootstrapNotifier {
    private static final String TAG = MyApplication.class.getSimpleName();

    private BackgroundPowerSaver backgroundPowerSaver;
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        // Configure BeaconManager which is a singleton
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundBetweenScanPeriod(0l);
        beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setBackgroundMode(true);
        beaconManager.setRegionStatePeristenceEnabled(false);

        // By default the AndroidBeaconLibrary will only find AltBeacons.
        // To find a different type of beacon, specify the byte layout for that beacon's advertisement.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BleUtil.LAYOUT_IBEACON));

        ArrayList regionList = new ArrayList<Region>();
        // Wake up the app when any beacon is seen
//        Region region = new Region("AnyBeacon", null, null, null);
//        regionList.add(region);
        regionBootstrap = new RegionBootstrap(this, regionList);

        // Automatically cause the BeaconLibrary to save battery whenever the application is not visible.
        backgroundPowerSaver = new BackgroundPowerSaver(this);

    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.d(TAG, "didDetermineStateForRegion(): " + String.valueOf(state) + " " + region.getUniqueId());
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "didEnterRegion(): " + region.getUniqueId());

//        //-- This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
//        //-- if you want the Activity to launch every single time beacons come into view, remove this call.
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("message", "Enter(): " + region.getUniqueId());
//        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
//        // created when a user launches the activity manually and it gets launched from here.
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);

        // Check if service is running in background
        Boolean status = Utils.isServiceRunning(
                this.getApplicationContext(),
                BeaconMonitoringService.class);

        if (!status) {
            Intent intent = new Intent(this, BeaconMonitoringService.class);
            this.startService(intent);
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "didExitRegion(): " + region.getUniqueId());

        // Check if service is running in background
        Boolean status = Utils.isServiceRunning(
                this.getApplicationContext(),
                BeaconMonitoringService.class);

        if (!status) {
            Intent intent = new Intent(this, BeaconMonitoringService.class);
            this.startService(intent);
        }
    }
}
