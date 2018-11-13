package de.uni_oldenburg.carfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.DetailsFragment;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.viewmodels.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout detailsFragmentContainer;
    private HistoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

        Toolbar myToolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        detailsFragmentContainer = findViewById(R.id.detailsHistoryFragmentContainer);

    }

    //TODO: Interface
    public void onParkingSpotSelected(ParkingSpot spot) {
        if (this.detailsFragmentContainer != null) {
            //running on Tablet
            DetailsFragment details = new DetailsFragment();
            details.getLifecycle().addObserver(new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                public void injectData() {
                    details.setData(spot);

                }
            });
            this.getSupportFragmentManager().beginTransaction().replace(R.id.detailsHistoryFragmentContainer, details).commit();

        } else {
            Intent intentDetails = new Intent(this, DetailsActivity.class);
            intentDetails.putExtra(Constants.EXTRA_PARKING_SPOT, spot);
            startActivity(intentDetails);
        }
    }
}
