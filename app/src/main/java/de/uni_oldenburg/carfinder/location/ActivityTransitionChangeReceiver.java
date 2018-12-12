package de.uni_oldenburg.carfinder.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import java.util.Date;

import androidx.core.content.ContextCompat;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.util.FileLogger;


public class ActivityTransitionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enhancedMode = prefs.getBoolean("pref_key_enhanced_recognition", false);

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            boolean entering = false;
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                FileLogger.getInstance().log(new Date().toString() + ": Received Activity transition: " + event.toString());

                if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    entering = true;
                }
            }
            FileLogger.getInstance().log("Enhanced mode enabled: " + enhancedMode);
            if (entering && enhancedMode) {
                Intent startService = new Intent(context, ForegroundLocationService.class);
                startService.putExtra(Constants.EXTRA_LOCATION_MODE, Constants.LOCATION_MODE_ENHANCED);
                ContextCompat.startForegroundService(context, startService);
            } else {
                if (enhancedMode) {
                    Intent stopService = new Intent(context, ForegroundLocationService.class);
                    context.stopService(stopService);
                } else {
                    Intent startService = new Intent(context, ForegroundLocationService.class);
                    ContextCompat.startForegroundService(context, startService);

                }
            }
        }


    }
}
