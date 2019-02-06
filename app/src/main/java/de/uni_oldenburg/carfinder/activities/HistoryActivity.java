package de.uni_oldenburg.carfinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.DetailsFragment;
import de.uni_oldenburg.carfinder.fragments.HistoryFragment;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.viewmodels.HistoryViewModel;

/**
 * HistoryActiviy: Zeigt die Parkplatz-History in einem RecyclerView an. Unterstützt einen Master-Detail Mode auf Tablets.
 */
public class HistoryActivity extends AppCompatActivity implements HistoryFragment.OnListFragmentInteractionListener {

    private LinearLayout detailsFragmentContainer;
    private HistoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

        Toolbar myToolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true); //Up-Navigation

        detailsFragmentContainer = findViewById(R.id.detailsHistoryFragmentContainer);

        if (detailsFragmentContainer != null) {
            this.viewModel.setMasterDetailMode(true);
            if (this.viewModel.getSelectedParkingSpot() != null) {
                this.onListFragmentInteraction(this.viewModel.getSelectedParkingSpot());
            }
        }

        ParkingSpotDatabaseManager.getAllParkingSpots(this, v -> onDataLoaded(v));


    }

    /**
     * Called when the database has finished loading.
     * @param data
     * @return
     */
    private Void onDataLoaded(List<ParkingSpot> data) {
        HistoryFragment history = new HistoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARGUMENT_SPOT_LIST, new ArrayList<>(data));
        history.setArguments(args);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.historyFragmentContainer, history).commit();
        return null;
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
            //Für den Master-Detail mode:
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

    @Override
    public void onListFragmentInteraction(ParkingSpot spot) {
        if (this.viewModel.isMasterDetailMode()) {
            //running on Tablet
            this.viewModel.setSelectedParkingSpot(spot);
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
