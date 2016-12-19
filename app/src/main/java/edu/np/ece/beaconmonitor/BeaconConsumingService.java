package edu.np.ece.beaconmonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

/**
 * Created by zqi2 on 17/12/16.
 */

public class BeaconConsumingService extends Service implements BeaconConsumer {
    private final String TAG = BeaconConsumingService.class.getSimpleName();
    private BeaconManager beaconManager;

    private static final String REGION_NAME = "TestBeacon";
    private static final String REGION_UUID = "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6";
    private static final String INTENT_NAME_TOAST = "edu.np.ece.beaconmonitor.toast";

    public BeaconConsumingService() {
        super();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        beaconManager.unbind(this);

        //-- Ask broadcast Receiver to restart service
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "onBeaconServiceConnect()");

        //-- Remove any region
        try {
            Region anyRegion = new Region("AnyBeacon", null, null, null);
            beaconManager.stopMonitoringBeaconsInRegion(anyRegion);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "didEnterRegion(): " + region.getUniqueId());

                //-- Test
                Intent broadcastIntent = new Intent(INTENT_NAME_TOAST);
                broadcastIntent.putExtra("message", "service.didEnterRegion()");
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "didExitRegion(): " + region.getUniqueId());
                //-- Test
                Intent broadcastIntent = new Intent(INTENT_NAME_TOAST);
                broadcastIntent.putExtra("message", "service.didExitRegion()");
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
            }
        });

        try {
            Identifier identifier = Identifier.parse(REGION_UUID);
            beaconManager.startMonitoringBeaconsInRegion(new Region(REGION_NAME, identifier, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}