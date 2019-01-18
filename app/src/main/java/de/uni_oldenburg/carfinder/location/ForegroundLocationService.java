package de.uni_oldenburg.carfinder.location;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.MainActivity;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.util.FileLogger;
import de.uni_oldenburg.carfinder.util.GeoUtils;

/**
 * This service is started to receive an accurate location very fast.
 * Should only be used to locate the user when an ActivityTransition is detected.
 */
public class ForegroundLocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private List<Location> locationList;
    private int mode = Constants.LOCATION_MODE_NORMAL;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        FileLogger.init(storageDir.getAbsolutePath());
        FileLogger.getInstance().log(new Date().toString() + ": Starting ForegroundLocationService in Mode: " + this.mode);
        if(intent == null){
            FileLogger.getInstance().log(new Date().toString() + ": Intent was null. Don't starting the service.");

        }
        this.locationList = new ArrayList<>();
        this.mode = intent.getIntExtra(Constants.EXTRA_LOCATION_MODE, Constants.LOCATION_MODE_NORMAL);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.foreground_loc_title))
                .setTicker(getString(R.string.foreground_loc_ticker))
                .setContentText(getString(R.string.foreground_loc_text))
                .setSmallIcon(R.drawable.compass_outline)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();

        startForeground(Constants.NOTIFICATION_ID_FOREGROUND_LOCATION,
                notification);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest request = new LocationRequest();
        request.setInterval(5000); // 5s interval
        request.setFastestInterval(5000);

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

        return Service.START_NOT_STICKY;
    }


    private void onLocationFinished(Location location, LocationCallback cb) {
        this.locationCallback = cb;
        Log.i(Constants.LOG_TAG, "Added location to list.");
        this.locationList.add(location);
        if (this.mode != Constants.LOCATION_MODE_ENHANCED) {
            this.stopSelf();
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

    @Override
    public void onDestroy() {
        Location loc = GeoUtils.getTransitionLocation(locationList);
        Address address = this.getAddressFromLocation(loc);
        ArrayList<String> addressFragments = new ArrayList<>();

        if (address != null) {
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            String addressString = TextUtils.join(System.getProperty("line.separator"),
                    addressFragments);
            FileLogger.getInstance().log(new Date().toString() + ": Result of ForegroundLocation was: " + addressString);
            if (this.mode != Constants.LOCATION_MODE_PERSIST_DIRECTLY) {
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
                        .setContentTitle(getString(R.string.notify_auto_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.notify_auto_content) + addressString + "?"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(notifyPendingIntent);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(Constants.NOTIFICATION_ID_PARKING_DETECTED, mBuilder.build());

            } else {
                ParkingSpot newSpot = new ParkingSpot(System.currentTimeMillis(), "Parkplatz", "Hinzugefügt durch Wearable-Erweiterung.", null, true, -1, loc.getLatitude(), loc.getLongitude(), addressString);
                ParkingSpotDatabaseManager.insertParkingSpot(newSpot, input -> null, this.getApplicationContext()); //TODO: Take care that this is the only currently used Parking spot.
            }
        }

        stopForeground(true);
        fusedLocationClient.removeLocationUpdates(this.locationCallback);
    }


}
