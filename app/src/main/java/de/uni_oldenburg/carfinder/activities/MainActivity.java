package de.uni_oldenburg.carfinder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.ExistingParkingSpotFragment;
import de.uni_oldenburg.carfinder.fragments.NewParkingSpotFragment;
import de.uni_oldenburg.carfinder.location.ActivityTransitionChangeReceiver;
import de.uni_oldenburg.carfinder.location.geocoding.AddressStringResultReceiver;
import de.uni_oldenburg.carfinder.location.geocoding.FetchAddressIntentService;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.places.GooglePlaces;
import de.uni_oldenburg.carfinder.places.PlacesResult;
import de.uni_oldenburg.carfinder.places.Result;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.util.FileLogger;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: Refactoring of parking meter stuff
//TODO: Manual set of Parking spot position
//TODO: Delete Button in DetailsView doesnt work
//TODO: onPause onResume in MainActivity
//TODO: Update main activity when user deletes current parking spot in history view.

//TODO: optional: bike support/ multiple cars
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private MainViewModel viewModel;
    private ExistingParkingSpotFragment existingParkingSpotFragment;
    private ProgressBar progressBar;
    private NewParkingSpotFragment newParkingSpotFragment;
    private Bitmap publicParkingIcon;
    private Marker currentMarker;

    //TODO: make this nicer
    private boolean initializedOwnPosition = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        FileLogger.init(storageDir.getAbsolutePath());

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        this.initUI();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        this.initializeObservers();

        //Loading icon for parking spots
        publicParkingIcon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.parking);
        publicParkingIcon = Bitmap.createScaledBitmap(
                publicParkingIcon, 100, 100, false); //TODO: fit to device size

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
            this.viewModel.getUpdatingPosition().postValue(false);

            this.sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }


        this.requestActivityTransitionUpdates(this);
        this.createNotificationChannel();


        //Geo related stuff:

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (viewModel.alreadyCheckedDatabase()) {
            this.loadExistingParkingSpotFragment();
            displayLocation();
            this.viewModel.getIsDatabaseLoaded().postValue(true);
        } else {
            ParkingSpotDatabaseManager.getAllParkingSpots(this, data -> this.onParkingSpotDatabaseLoaded(data));

        }

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
                e -> Toast.makeText(MainActivity.this, getString(R.string.activity_recognition_na), Toast.LENGTH_LONG).show());

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
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
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


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        this.viewModel.getIsMapLoaded().postValue(true);
    }


    /**
     * Displays a marker on given position on the map.
     *
     * @param lat
     * @param lon
     */
    public void displayMarkerOnMap(double lat, double lon, String title) {
        if (mMap != null) {
            if (this.currentMarker != null)
                this.currentMarker.remove();
            if (!this.initializedOwnPosition) { //Find a better solution
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lat, lon), Constants.DEFAULT_ZOOM));
                this.initializedOwnPosition = true;
            }
            // Add a marker in Germany, and move the camera.
            LatLng position = new LatLng(lat, lon);
            this.currentMarker = mMap.addMarker(new MarkerOptions().position(position).title(title));
        }
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
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if parking spot places prefs has changed:
        if (this.viewModel.getIsLoadingDone().getValue() != null && this.viewModel.getIsLoadingDone().getValue()) {
            SharedPreferences sharedPreferences =
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            Boolean showSpots = sharedPreferences.getBoolean("pref_key_surr_spots", false);

            for (Marker m : this.viewModel.getPublicParkingSpots()) {
                m.remove();
            }
            if (showSpots) {
                displaySurroundingSpots();
            }
        }

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
            viewModel.getCurrentPositionLat().setValue(currentSpot.getLatitude()); //Why?
            viewModel.getCurrentPositionLon().setValue(currentSpot.getLongitude());
            loadExistingParkingSpotFragment();
            displayLocation();
        } else {
            viewModel.setParkingSpotSaved(false);
            displayLocation();
        }

        this.viewModel.getIsDatabaseLoaded().postValue(true);

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
    public void loadNewParkingSpotFragment() {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            newParkingSpotFragment = new NewParkingSpotFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.stateFragmentContainer, newParkingSpotFragment).commit();
        }

    }

    /**
     * Initializes the MainActivity UI.
     */
    private void initUI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        this.progressBar = findViewById(R.id.progressBar);
        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        sheetBehavior.setHideable(false);
    }

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Call this method to display the current address and position in the UI.
     */
    public void displayLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            LocationRequest request = new LocationRequest();
            request.setInterval(5000); // 5s interval
            request.setFastestInterval(5000);

            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null || locationResult.getLastLocation() == null) {
                        Log.w(Constants.LOG_TAG, "Failed to display current Location");
                        return;
                    }
                    // Set the map's camera position to the current location of the device.
                    Location lastKnownLocation = locationResult.getLastLocation();
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
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            AddressStringResultReceiver resultReceiver = new AddressStringResultReceiver(data -> this.onAddressResultReceived(data, lastKnownLocation));
            Intent intent = new Intent(this, FetchAddressIntentService.class);
            intent.putExtra(Constants.ADDRESS_RECEIVER, resultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastKnownLocation);
            startService(intent);
        }
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

        if (this.progressBar.getVisibility() == View.VISIBLE) {
            //First time the position was received
            this.progressBar.setVisibility(View.INVISIBLE);
            this.loadNewParkingSpotFragment();
        }

        //Update the LiveData to display the changes in UI
        this.viewModel.getCurrentPositionLat().postValue(location.getLatitude());
        this.viewModel.getCurrentPositionLon().postValue(location.getLongitude());
        this.viewModel.getCurrentPositionAddress().postValue(address);
        if (!this.viewModel.getIsPositionReady().getValue())
            this.viewModel.getIsPositionReady().postValue(true);


        return null;
    }

    /**
     * Initializes the Observers with it's callbacks that are called on special async events.
     */
    private void initializeObservers() {

        this.viewModel.getIsMapLoaded().setValue(false);
        this.viewModel.getIsDatabaseLoaded().setValue(false);
        this.viewModel.getIsPositionReady().setValue(false);

        final Observer<Boolean> loadingStateObserver = loaded -> {
            if (loaded) {
                //When everything is loaded: display the current parking spot, if available
                if (viewModel.getCurrentPositionLon().getValue() != null) {
                    if (this.viewModel.isParkingSpotSaved())
                        displayMarkerOnMap(MainActivity.this.viewModel.getParkingSpot().getLatitude(), MainActivity.this.viewModel.getParkingSpot().getLongitude(), this.viewModel.getParkingSpot().getName());
                }

                //Load surrounding parking spots into the map.
                SharedPreferences sharedPreferences =
                        androidx.preference.PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                Boolean showSpots = sharedPreferences.getBoolean("pref_key_surr_spots", false);

                if (showSpots) {
                    displaySurroundingSpots();
                }

                //Zoom to own Location TODO: Only in no parking spot state
                if (!this.viewModel.isParkingSpotSaved()) {
                    LatLng ownPosition = new LatLng(this.viewModel.getCurrentPositionLat().getValue(), this.viewModel.getCurrentPositionLon().getValue());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ownPosition, Constants.DEFAULT_ZOOM));
                }

            }
        };
        final Observer<Boolean> positionUpdateSettingObserver = update -> {
            if (this.viewModel.getIsLoadingDone().getValue() && update) {
                displayLocation();
            } else if (this.fusedLocationClient != null) {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };
        this.viewModel.getUpdatingPosition().observe(this, positionUpdateSettingObserver);
        this.viewModel.getIsLoadingDone().observe(this, loadingStateObserver);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.fusedLocationClient != null)
            this.fusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        ParkingSpotDatabaseManager.getAllParkingSpots(this, data -> this.onParkingSpotDatabaseLoaded(data));
    }

    private void displaySurroundingSpots() {
        GooglePlaces.getInstance().getNearbyParkingPlaces(MainActivity.this.viewModel.getCurrentPositionLat().getValue(), MainActivity.this.viewModel.getCurrentPositionLon().getValue(), new Callback<PlacesResult>() {
            @Override
            public void onResponse(Call<PlacesResult> call, Response<PlacesResult> response) {
                for (Marker m : MainActivity.this.viewModel.getPublicParkingSpots())
                    m.remove();
                for (Result r : response.body().getResults()) {
                    Log.i(Constants.LOG_TAG, r.getName() + ": " + r.getGeometry().getLocation().getLat() + ", " + r.getGeometry().getLocation().getLng());

                    LatLng pos = new LatLng(r.getGeometry().getLocation().getLat(), r.getGeometry().getLocation().getLng());

                    MainActivity.this.viewModel.getPublicParkingSpots().add(MainActivity.this.mMap.addMarker(new MarkerOptions().position(pos)
                            .title(r.getName()).icon(BitmapDescriptorFactory.fromBitmap(MainActivity.this.publicParkingIcon))));
                }
            }

            @Override
            public void onFailure(Call<PlacesResult> call, Throwable t) {
                Log.e(Constants.LOG_TAG, "Failed to query Places API:" + t.getLocalizedMessage());
            }
        });
    }

}
