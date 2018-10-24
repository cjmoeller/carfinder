package carfinder.uni_oldenburg.de.carfinder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "62387";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.requestActivityTransitionUpdates(this);
        this.createNotificationChannel();
    }

    /**
     * Requests Activity Recognition Updates. See <a href="https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionClient.html#requestActivityUpdates(long,%20android.app.PendingIntent">Google API Reference</a>
     *
     * @param context
     */
    private void requestActivityTransitionUpdates(final Context context) {
        Intent intent = new Intent(this, ActivityTransitionChangeReceiver.class);
        intent.setAction("de.uni_oldenburg.carfinder.TRANSITION");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 34, intent, 0);

        ActivityTransitionRequest request = buildTransitionRequest();
        Task task = ActivityRecognition.getClient(context)
                .requestActivityTransitionUpdates(request, pendingIntent);
        task.addOnSuccessListener(
                new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(MainActivity.this, "Now Listening for Activity Recognition Updates", Toast.LENGTH_LONG).show();
                    }

                });
        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to start Listening for Activity Recognition Updates", Toast.LENGTH_LONG).show();
                    }
                });

    }

    /**
     * Generates a TransitionRequest for ActivityRecognition.
     *
     * @return the request
     */
    private ActivityTransitionRequest buildTransitionRequest() {
        List transitions = new ArrayList<>();

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        return new ActivityTransitionRequest(transitions);
    }

    /**
     * Creates a Notification Channel for the App (required for Android 8 and higher).
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
