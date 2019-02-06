package de.uni_oldenburg.carfinder.location;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.MainActivity;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.web.ors.ORSResult;
import de.uni_oldenburg.carfinder.web.ors.OpenRouteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * TimePickerLocationService: Wird genutzt um im Hintergrund intelligent zu berechnen, wann ein Nutzer den
 * Weg zu seinem Auto antreten sollte, um vor Ablauf der Parkuhr anzukommen.
 */
public class TimePickerLocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private double parkingSpotLat;
    private double parkingSpotLon;
    private long parkingSpotExpirationTime;
    private LocationCallback locationCallback;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Log.d(Constants.LOG_TAG, "Starting Time Picker Location Service caused by auto mode");
        this.parkingSpotLat = intent.getExtras().getDouble("lat");
        this.parkingSpotLon = intent.getExtras().getDouble("lon");
        this.parkingSpotExpirationTime = intent.getExtras().getLong("expr");

        fusedLocationClient = getFusedLocationProviderClient(this);

        LocationRequest request = new LocationRequest();
        request.setInterval(1000 * 60 * 5); // 1min interval
        request.setFastestInterval(1000 * 60 * 5);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                } else {
                    Location loc = locationResult.getLastLocation();
                    Log.i(Constants.LOG_TAG, "Received Location in TimepickerLocationService");
                    OpenRouteService.getInstance().getRoute(loc.getLatitude(), loc.getLongitude(),
                            TimePickerLocationService.this.parkingSpotLat, TimePickerLocationService.this.parkingSpotLon,
                            new Callback<ORSResult>() {
                                @Override
                                public void onResponse(Call<ORSResult> call, Response<ORSResult> response) {
                                    Log.d(Constants.LOG_TAG, "Received response from ORS API");
                                    ORSResult result = response.body();
                                    if (result != null && result.getRoutes().get(0) != null) {
                                        Log.d(Constants.LOG_TAG, "Time difference is:" + (result.getRoutes().get(0).getSummary().getDuration() * 1000 +
                                                System.currentTimeMillis() + 5 * 60 * 1000 - TimePickerLocationService.this.parkingSpotExpirationTime));
                                        if (result.getRoutes().get(0).getSummary().getDuration() * 1000 +
                                                System.currentTimeMillis() > TimePickerLocationService.this.parkingSpotExpirationTime - 5 * 60 * 1000) {
                                            TimePickerLocationService.this.sendNotification();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ORSResult> call, Throwable t) {
                                    Log.e(Constants.LOG_TAG, t.getLocalizedMessage());
                                }
                            });

                }

            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        } catch (SecurityException e) {
            Log.e(Constants.LOG_TAG, "Permission to update location was not given.");
        }

        return Service.START_NOT_STICKY;
    }

    private void sendNotification() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.car)
                .setContentTitle(getString(R.string.intelligent_expiration))
                .setContentText(getString(R.string.intelligent_expiration_desc))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
        this.fusedLocationClient.removeLocationUpdates(locationCallback);
        this.stopSelf();
    }

}

