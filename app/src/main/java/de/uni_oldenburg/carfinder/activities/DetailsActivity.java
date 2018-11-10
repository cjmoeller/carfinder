package de.uni_oldenburg.carfinder.activities;

import android.os.Bundle;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.DetailsFragment;
import de.uni_oldenburg.carfinder.fragments.ExistingParkingSpotFragment;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.Constants;

public class DetailsActivity extends AppCompatActivity {

    //TODO: Viewmodel or save instance state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar myToolbar = findViewById(R.id.toolbar_details);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ParkingSpot data = (ParkingSpot) this.getIntent().getSerializableExtra(Constants.EXTRA_PARKING_SPOT);

        DetailsFragment details = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.detailsActFragment);

        DetailsFragment finalDetails = details;
        details.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            public void detailsFragmentStarted() {
                finalDetails.setData(data);

            }
        });
    }
}
