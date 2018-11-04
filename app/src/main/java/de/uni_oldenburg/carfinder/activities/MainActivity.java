package de.uni_oldenburg.carfinder.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.location.ForegroundLocationService;
import de.uni_oldenburg.carfinder.location.geocoding.AddressStringResultReceiver;
import de.uni_oldenburg.carfinder.fragments.ExistingParkingSpotFragment;
import de.uni_oldenburg.carfinder.fragments.NewParkingSpotFragment;
import de.uni_oldenburg.carfinder.location.ActivityTransitionChangeReceiver;
import de.uni_oldenburg.carfinder.location.geocoding.FetchAddressIntentService;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private MainViewModel viewModel;
    private ExistingParkingSpotFragment existingParkingSpotFragment;
    private ProgressBar progressBar;
    private NewParkingSpotFragment newParkingSpotFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        this.initUI();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        //MainActivity was started from "automatically detected parking spot" notification.
        if (getIntent().getBooleanExtra(Constants.CREATE_NEW_ENTRY_EXTRA, false)) {
            //Address from the ForegroundLocationService.
            Address address = getIntent().getParcelableExtra(Constants.ADDRESS_EXTRA);
            ArrayList<String> addressFragments = new ArrayList<>();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            String addressString = TextUtils.join(System.getProperty("line.separator"),
                    addressFragments);
            this.viewModel.getParkingSpot().setAddress(addressString);
            this.viewModel.getParkingSpot().setLatitude(address.getLatitude());
            this.viewModel.getParkingSpot().setLongitude(address.getLongitude());

            this.sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }


        this.requestActivityTransitionUpdates(this);
        this.createNotificationChannel();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (viewModel.alreadyCheckedDatabase()) {
            this.loadExistingParkingSpotFragment();
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


    /**
     * Called when the database was queried for parking spot data.
     *
     * @param data
     * @return
     */
    public Void onParkingSpotDatabaseLoaded(List<ParkingSpot> data) {
        ParkingSpot currentSpot = null;
        for (ParkingSpot spot : data) {
            if (spot.isCurrentlyUsed()) {
                currentSpot = spot;
                break;
            }
        }
        viewModel.setCheckedDatabase(true);
        if (currentSpot != null) {
            viewModel.setParkingSpot(currentSpot);
            viewModel.setParkingSpotSaved(true);
            loadExistingParkingSpotFragment();
        } else {
            viewModel.setParkingSpotSaved(false);
            displayLocation();
        }

        return null;
    }

    /**
     * Loads the fragment to display details about an existing parking spot (viewmodel).
     */
    public void loadExistingParkingSpotFragment() {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            this.progressBar.setVisibility(View.INVISIBLE);
            if (this.fusedLocationClient != null) //don't update Position if user has parked
                this.fusedLocationClient.removeLocationUpdates(this.locationCallback);
            existingParkingSpotFragment = new ExistingParkingSpotFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.stateFragmentContainer, existingParkingSpotFragment).commit();
        }

    }

    /**
     * Loads the fragment to create a new parking spot.
     */
    private void loadNewParkingSpotFragment() {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            newParkingSpotFragment = new NewParkingSpotFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.stateFragmentContainer, newParkingSpotFragment).commit();
        }

    }

    private void initUI() {
        this.progressBar = findViewById(R.id.progressBar);
        this.initializeBottomSheetMenu();
    }

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Call this method to display the current location including the address.
     */
    private void displayLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            LocationRequest request = new LocationRequest();
            request.setInterval(5000); // 5s interval
            request.setFastestInterval(5000);

            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (mMap == null || locationResult == null || locationResult.getLastLocation() == null) {
                        Log.w(Constants.LOG_TAG, "Failed to display current Location");
                        return;
                    }
                    // Set the map's camera position to the current location of the device.
                    Location lastKnownLocation = locationResult.getLastLocation();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()), Constants.DEFAULT_ZOOM));
                    // Add a marker in Germany, and move the camera.
                    LatLng position = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(position).title("Standort"));
                    loadAddressFromLocation(lastKnownLocation);

                }
            };
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        } catch (SecurityException e) {
            Log.e(Constants.LOG_TAG, "No GPS Permission");
        }
    }

    /**
     * Starts an FetchAddressIntentService to receive the Address of a given location.
     *
     * @param lastKnownLocation
     */
    private void loadAddressFromLocation(Location lastKnownLocation) {
        AddressStringResultReceiver resultReceiver = new AddressStringResultReceiver(data -> this.onAddressResultReceived(data, lastKnownLocation));
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.ADDRESS_RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastKnownLocation);
        startService(intent);
    }

    /**
     * Called when an address result for the current position is received
     *
     * @param address
     * @param location
     * @return
     */
    private Void onAddressResultReceived(String address, Location location) {
        //replaces the loader with the new parkin Spot fragment.

        this.viewModel.getParkingSpot().setAddress(address);
        this.viewModel.getParkingSpot().setLatitude(location.getLatitude());
        this.viewModel.getParkingSpot().setLongitude(location.getLongitude());

        if (this.progressBar.getVisibility() == View.VISIBLE) {
            //First time the position was received
            this.progressBar.setVisibility(View.INVISIBLE);
            this.loadNewParkingSpotFragment();
        }

        //Update the LiveData to display the changes in UI
        this.viewModel.getCurrentPositionLat().postValue(location.getLatitude());
        this.viewModel.getCurrentPositionLon().postValue(location.getLongitude());
        this.viewModel.getCurrentPositionAddress().postValue(address);

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.fusedLocationClient != null)
            this.fusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

}
