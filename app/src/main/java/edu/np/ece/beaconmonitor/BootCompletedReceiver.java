package edu.np.ece.beaconmonitor;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zqi2 on 17/12/16.
 */

public class BootCompletedReceiver extends WakefulBroadcastReceiver {
    static final String TAG = BootCompletedReceiver.class.getSimpleName();

    public BootCompletedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) ||
                "edu.np.beaconmonitor.RestartService".equals(intent.getAction())) {

            // Start the service, keeping the device awake while it is launching.
            Log.i("BootCompletedReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
            Toast.makeText(context, "BootCompletedReceiver.onReceive()", Toast.LENGTH_LONG).show();

            // Check if service is running in background
            Boolean status = Utils.isServiceRunning(
                    context,
                    BeaconMonitoringService.class);
            if (!status) {
                Intent service = new Intent(context, BeaconMonitoringService.class);
                startWakefulService(context, service);
            }
        }

//        Log.i(BeaconMonitoringService.class.getSimpleName(), "BootCompletedReceiver starting Service...");
//        context.startService(new Intent(context, BeaconMonitoringService.class));
    }
}
