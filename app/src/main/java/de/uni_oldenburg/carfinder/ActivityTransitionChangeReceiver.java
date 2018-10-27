package de.uni_oldenburg.carfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import de.uni_oldenburg.carfinder.activities.MainActivity;

public class ActivityTransitionChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // Do something useful here...
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.common_full_open_on_phone)
                    .setContentTitle("ActivityTransition")
                    .setContentText("Activity Transition erkannt")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify(0, mBuilder.build());
        }


    }
}
