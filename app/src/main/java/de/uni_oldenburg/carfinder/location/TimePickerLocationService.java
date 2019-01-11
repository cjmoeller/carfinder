package de.uni_oldenburg.carfinder.location;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import android.util.Log;


import androidx.annotation.Nullable;
import de.uni_oldenburg.carfinder.util.AlarmReceiver;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.util.OpenRouteServiceApi;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TimePickerLocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private Location start;
    private Location dest;
    private float distance;
    private long time;
    private long alarmtime;
    private OpenRouteServiceApi apiService;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }


    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        apiService = new OpenRouteServiceApi();
        apiService.setDestAdresse((double)intent.getExtras().get("lat"), (double)intent.getExtras().get("lon"));
        fusedLocationClient = getFusedLocationProviderClient(this);
        LocationRequest request = new LocationRequest();
        request.setInterval(1000 * 60 * 5); // 5min interval
        request.setFastestInterval(1000 * 60 * 5);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(request);
        LocationSettingsRequest settingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(settingsRequest);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }else{
                
                    apiService.createNewAdresse(start.getLatitude(), start.getLongitude());
                    apiService.executeReqeustCall();
                    time = (long)apiService.getDuration() * 1000;

                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    alarmtime = alarmManager.getNextAlarmClock().getTriggerTime(); //get "old" Alarm
                    alarmtime = alarmtime - time;//substract calculated time
                    cancelAlarm(alarmManager);//cancel old alarm
                    setAlarm(alarmManager, alarmtime);//set new alarm

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

    private void cancelAlarm(AlarmManager alarmManager){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm(AlarmManager alarmManager, long alarmtime){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmtime*60*1000, pendingIntent);
    }


    }

