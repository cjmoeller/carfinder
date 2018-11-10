package de.uni_oldenburg.carfinder.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import de.uni_oldenburg.carfinder.location.ForegroundLocationService;
import de.uni_oldenburg.carfinder.util.Constants;

/**
 * Receives Broadcast for bluetooth events such as 'device connected' and 'device disconnected'.
 */
public class BluetoothChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) || intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> configuredDevices = sharedPreferences.getStringSet("pref_key_bluetooth_device", null);
            if (configuredDevices.contains(device.getAddress())) {
                //A user configured device was disconnected. So probably the car was parked.
                Log.i(Constants.LOG_TAG, "Bluetooth device disconnected!");
                Intent startService = new Intent(context, ForegroundLocationService.class);
                ContextCompat.startForegroundService(context, startService);
            }
        }
    }
}
