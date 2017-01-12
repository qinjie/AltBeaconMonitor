package edu.np.ece.beaconmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zqi2 on 17/12/16.
 */

public class BeaconMonitoringService extends Service implements BeaconConsumer {
    private final String TAG = BeaconMonitoringService.class.getSimpleName();
    private BeaconManager beaconManager;

    public BeaconMonitoringService() {
        super();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        Toast.makeText(this, "Create Beacon Monitoring Service...", Toast.LENGTH_SHORT).show();
        super.onCreate();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "Destroy Beacon Monitoring Service...", Toast.LENGTH_SHORT).show();

        beaconManager.unbind(this);

        //-- Ask broadcast Receiver to restart service
        Intent broadcastIntent = new Intent("edu.np.beaconmonitor.RestartService");
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand()");

        // Release the wakelock holded by BootCompletedReceiver
        Log.i(BootCompletedReceiver.class.getSimpleName(), "Completed service @ " + SystemClock.elapsedRealtime());
        if (intent != null) {
            BootCompletedReceiver.completeWakefulIntent(intent);
        }
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

                //-- Test by create notification
                String[] s = {
                        region.getId1() == null ? "" : region.getId1().toString().substring(0, 9),
                        region.getId2() == null ? "" : region.getId2().toString(),
                        region.getId3() == null ? "" : region.getId3().toString()
                };
                String subject = TextUtils.join(",", s);
                String title = "In Region " + region.getUniqueId();
                //Use the hashcode of current timestamp mixed with some string to make it unique
                int requestCode = ("someString" + System.currentTimeMillis()).hashCode();
                createNotification(title, subject, requestCode);
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "didExitRegion(): " + region.getUniqueId());

                //-- Test by create notification
                String[] s = {
                        region.getId1() == null ? "" : region.getId1().toString().substring(0, 9),
                        region.getId2() == null ? "" : region.getId2().toString(),
                        region.getId3() == null ? "" : region.getId3().toString(),
                };
                String subject = TextUtils.join(",", s);
                String title = "Exit Region " + region.getUniqueId();
                //Use the hashcode of current timestamp mixed with some string to make it unique
                int requestCode = ("someString" + System.currentTimeMillis()).hashCode();
                createNotification(title, subject, requestCode);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
            }
        });

        // Get beacon list from strings.xml

        Toast.makeText(this, "Register Beacons...", Toast.LENGTH_SHORT).show();
        List<String> beacons = Arrays.asList(getResources().getStringArray(R.array.beacon_list));
        for (String str : beacons) {
            String[] sa = str.split(":", -1);

            if (sa.length != 4) {
                Toast.makeText(this, "Invalid Beacon ID: " + str, Toast.LENGTH_LONG).show();
                continue;
            }

            try {
                Identifier id1 = null;
                Identifier id2 = null;
                Identifier id3 = null;
                String name = sa[0];
                if (!TextUtils.isEmpty(sa[1]))
                    id1 = Identifier.parse(sa[1]);
                if (!TextUtils.isEmpty(sa[2]))
                    id2 = Identifier.parse(sa[2]);
                if (!TextUtils.isEmpty(sa[3]))
                    id3 = Identifier.parse(sa[3]);

                beaconManager.startMonitoringBeaconsInRegion(new Region(name, id1, id2, id3));
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void createNotification(String title, String subject, int requestCode) {

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntent(new Intent(this, RegionDetail.class));
//        PendingIntent pendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );

        // build notification
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(subject)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(requestCode, n);
    }
}