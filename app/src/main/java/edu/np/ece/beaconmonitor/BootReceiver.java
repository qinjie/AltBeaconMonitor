package edu.np.ece.beaconmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zqi2 on 17/12/16.
 */

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BeaconConsumingService.class.getSimpleName(), "BootReceiver starting Service...");
        context.startService(new Intent(context, BeaconConsumingService.class));
    }
}
