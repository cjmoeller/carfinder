package de.uni_oldenburg.carfinder.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import androidx.core.content.ContextCompat;


public class ActivityTransitionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // Do something useful here...
            }

            Intent startService = new Intent(context, ForegroundLocationService.class);
            ContextCompat.startForegroundService(context, startService);
        }


    }
}
