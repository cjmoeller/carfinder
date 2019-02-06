package de.uni_oldenburg.carfinder.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.SettingsFragment;

/**
 * SettingsActivity: Lädt die Settings als Fragment
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

       Toolbar myToolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_content, new SettingsFragment())
                .commit();
    }

}
