package edu.np.ece.beaconmonitor;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by zqi2 on 12/01/2017.
 */

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
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

}
