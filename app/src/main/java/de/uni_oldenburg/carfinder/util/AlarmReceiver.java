package de.uni_oldenburg.carfinder.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.MainActivity;
import de.uni_oldenburg.carfinder.location.TimePickerLocationService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //
        Vibrator vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(1000);

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra(Constants.ALARM_EXPIRED_INTENT, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.car)
                .setContentTitle(context.getString(R.string.channel_name))
                .setContentText(context.getString(R.string.notify_park_clock))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, mBuilder.build());

        Intent serviceIntent = new Intent(context, TimePickerLocationService.class);
        context.stopService(serviceIntent);
    }

}
