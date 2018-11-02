package de.uni_oldenburg.carfinder.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.location.ActivityTransitionChangeReceiver;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.ExistingParkingSpotFragment;
import de.uni_oldenburg.carfinder.fragments.NewParkingSpotFragment;
import de.uni_oldenburg.carfinder.location.ForegroundLocationService;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        this.initializeBottomSheetMenu();

        if (getIntent().getBooleanExtra(Constants.CREATE_NEW_ENTRY_EXTRA, false)) {
            this.sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        this.requestActivityTransitionUpdates(this);
        this.createNotificationChannel();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (viewModel.alreadyCheckedDatabase()) {
            this.loadExistingParkingSpotFragment(viewModel.getCurrentSpot());
        } else {
            ParkingSpotDatabaseManager.getAllParkingSpots(this, data -> this.onParkingSpotDatabaseLoaded(data));

        }

        //ParkingSpot test = new ParkingSpot(System.currentTimeMillis(), "Parkplatzname", "Links neben LIDL", null, true, -1, 53, 8, "Adresse 23, 27123 Stadt");
        //ParkingSpotDatabaseManager.insertParkingSpot(test, this);


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
        task.addOnFailureListener(
                e -> Toast.makeText(MainActivity.this, "Automatic parking spot detection is not available on your device.", Toast.LENGTH_LONG).show());

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
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Germany, and move the camera.
        LatLng sydney = new LatLng(53,8);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Parkplatz"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history:
                Intent intentHistory = new Intent(this, HistoryActivity.class);
                startActivity(intentHistory);
                return true;

            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void initializeBottomSheetMenu() {
        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        sheetBehavior.setHideable(false);
    }


    public Void onParkingSpotDatabaseLoaded(List<ParkingSpot> data) {
        ParkingSpot currentSpot = null;
        for (ParkingSpot spot : data) {
            if (spot.isCurrentlyUsed()) {
                currentSpot = spot;
                break;
            }
        }
        viewModel.setCurrentSpot(currentSpot);
        viewModel.setCheckedDatabase(true);
        if (currentSpot != null) {
            loadExistingParkingSpotFragment(currentSpot);
        } else {
            loadNewParkingSpotFragment();
        }

        return null;
    }

    /**
     * Loads the fragment to display details about an existing parking spot.
     * @param currentSpot
     */
    private void loadExistingParkingSpotFragment(ParkingSpot currentSpot) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PARKING_SPOT_OBJECT_BUNDLE, currentSpot);
        ExistingParkingSpotFragment existingParkingSpotFragment = new ExistingParkingSpotFragment();
        existingParkingSpotFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.stateFragmentContainer, existingParkingSpotFragment).commit();
    }

    /**
     * Loads the fragment to create a new parking spot.
     */
    private void loadNewParkingSpotFragment(){
        NewParkingSpotFragment newParkingSpotFragment = new NewParkingSpotFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.stateFragmentContainer, newParkingSpotFragment).commit();

    }

}
