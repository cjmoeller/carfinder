package de.uni_oldenburg.carfinder.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import androidx.core.content.ContextCompat;
import de.uni_oldenburg.carfinder.location.ForegroundLocationService;

public class DataLayerListenerService extends WearableListenerService {
    private static final String TAG = "DataLayerSample";


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }


        // Loop through the events and send a message
        // to the node that created the data item.
        for (DataEvent event : dataEvents) {
            Uri uri = event.getDataItem().getUri();


        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/park")) {
            Intent startService = new Intent(this.getApplicationContext(), ForegroundLocationService.class);
            startService.putExtra(Constants.EXTRA_LOCATION_MODE, Constants.LOCATION_MODE_PERSIST_DIRECTLY);
            ContextCompat.startForegroundService(this.getApplicationContext(), startService);
        }
    }
}
