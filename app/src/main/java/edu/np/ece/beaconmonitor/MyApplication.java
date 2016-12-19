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

        backgroundPowerSaver = new BackgroundPowerSaver(this);
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundBetweenScanPeriod(0l);
        beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setBackgroundMode(true);
        beaconManager.setRegionStatePeristenceEnabled(false);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BleUtil.LAYOUT_IBEACON));

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("AnyBeacon", null, null, null);
        ArrayList regionList = new ArrayList<Region>();
        regionList.add(region);
        regionBootstrap = new RegionBootstrap(this, regionList);

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

        Intent intent = new Intent(this, BeaconConsumingService.class);
        this.startService(intent);
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "didExitRegion(): " + region.getUniqueId());

        Intent intent = new Intent(this, BeaconConsumingService.class);
        this.startService(intent);

    }
}
