package de.uni_oldenburg.carfinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.DetailsFragment;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.Constants;

public class DetailsActivity extends AppCompatActivity {

    //TODO: Viewmodel or save instance state
    private ParkingSpot data;

    private TextView address;
    private TextView position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar myToolbar = findViewById(R.id.toolbar_details);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.data = (ParkingSpot) this.getIntent().getSerializableExtra(Constants.EXTRA_PARKING_SPOT);

        this.initUI();
    }

    private void initUI() {
        DetailsFragment details = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.detailsActFragment);

        DetailsFragment finalDetails = details;
        details.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            public void detailsFragmentStarted() {
                finalDetails.setData(data);

            }
        });

        address = findViewById(R.id.detailsAddress);
        position = findViewById(R.id.detailsPos);

        address.setText(this.data.getAddress());
        position.setText(this.data.getLatitude() + ", " + this.data.getLongitude());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + this.data.getLatitude() + "," + this.data.getLongitude() + "(" + this.data.getName() + ")" + "&z=" + Constants.DEFAULT_ZOOM);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;
            case R.id.action_share:
                Intent actionIntent = new Intent();
                actionIntent.setAction(Intent.ACTION_SEND);
                actionIntent.setType("text/plain");
                actionIntent.putExtra(Intent.EXTRA_TEXT, "Hey, ich habe hier geparkt: " + Constants.MAP_SHARE_URL + this.data.getLatitude() + "," + this.data.getLongitude());
                this.startActivity(actionIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
