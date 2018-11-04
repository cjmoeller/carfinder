package de.uni_oldenburg.carfinder.location;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.MainActivity;
import de.uni_oldenburg.carfinder.util.Constants;

/**
 * This service is started to receive an accurate location very fast.
 * Should only be used to locate the user when an ActivityTransition is detected.
 */
public class ForegroundLocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("CarFinder Location")
                .setTicker("CarFinder Location")
                .setContentText("Trying to locate your parking spot...")
                .setSmallIcon(R.drawable.compass_outline)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();

        startForeground(Constants.NOTIFICATION_ID_FOREGROUND_LOCATION,
                notification);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest request = new LocationRequest();
        request.setInterval(10000); // 10s interval
        request.setFastestInterval(10000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                ForegroundLocationService.this.onLocationFinished(locationResult.getLastLocation(), this);
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        } catch (SecurityException e) {
            Log.e(Constants.LOG_TAG, "Permission to update location was not given.");
        }
        return Service.START_STICKY;
    }

    private void onLocationFinished(Location location, LocationCallback cb) {
        Address address = this.getAddressFromLocation(location);
        ArrayList<String> addressFragments = new ArrayList<>();

        if (address != null) {
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            String addressString = TextUtils.join(System.getProperty("line.separator"),
                    addressFragments);


            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.putExtra(Constants.CREATE_NEW_ENTRY_EXTRA, true);
            notifyIntent.putExtra(Constants.ADDRESS_EXTRA, address);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.car)
                    .setContentTitle("Parkplatz automatisch erkannt.")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Haben Sie an diesem Ort geparkt: " + addressString + "?"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(notifyPendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(Constants.NOTIFICATION_ID_PARKING_DETECTED, mBuilder.build());
            stopForeground(true);
            fusedLocationClient.removeLocationUpdates(cb);
        }
    }

    /**
     * Retrieves an Address from a location via Reverse Geocoding.
     */
    private Address getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            Log.e(Constants.LOG_TAG, "IO Error", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {

            Log.e(Constants.LOG_TAG, "Invalid Lat/Long" + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            Log.e(Constants.LOG_TAG, "No Address found.");
        } else {
            Address address = addresses.get(0);
            return address;
        }
        return null;
    }
}
