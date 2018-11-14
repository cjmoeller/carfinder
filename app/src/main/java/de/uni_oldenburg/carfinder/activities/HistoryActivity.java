package de.uni_oldenburg.carfinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
        if (detailsFragmentContainer != null) {
            this.viewModel.setMasterDetailMode(true);
        }

    }

    //TODO: Interface
    public void onParkingSpotSelected(ParkingSpot spot) {
        if (this.viewModel.isMasterDetailMode()) {
            //running on Tablet


            invalidateOptionsMenu();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.viewModel.isMasterDetailMode()) {
            getMenuInflater().inflate(R.menu.menu_details, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.viewModel.getSelectedParkingSpot() == null)
            return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.action_map:
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + this.viewModel.getSelectedParkingSpot().getLatitude() + "," +
                        this.viewModel.getSelectedParkingSpot().getLongitude() + "(" + this.viewModel.getSelectedParkingSpot().getName() + ")" + "&z=" + Constants.DEFAULT_ZOOM);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;
            case R.id.action_share:
                Intent actionIntent = new Intent();
                actionIntent.setAction(Intent.ACTION_SEND);
                actionIntent.setType("text/plain");
                actionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + Constants.MAP_SHARE_URL +
                        this.viewModel.getSelectedParkingSpot().getLatitude() + "," + this.viewModel.getSelectedParkingSpot().getLongitude());
                this.startActivity(actionIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
