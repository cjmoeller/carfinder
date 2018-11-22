package de.uni_oldenburg.carfinder.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.net.Uri;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Alarm wurde erstellt!", Toast.LENGTH_LONG).show();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
        Vibrator vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(1000);
    }
}
