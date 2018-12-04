package de.uni_oldenburg.carfinder.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import androidx.core.content.ContextCompat;


public class ActivityTransitionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            boolean entering = false;
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                    entering = true;
                }
            }
            if (entering) {
                Intent startService = new Intent(context, ForegroundLocationService.class);
                ContextCompat.startForegroundService(context, startService);
            } else {
                Intent stopService = new Intent(context, ForegroundLocationService.class);
                context.stopService(stopService);
            }
        }


    }
}
