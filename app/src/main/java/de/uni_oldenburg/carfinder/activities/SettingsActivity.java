package de.uni_oldenburg.carfinder.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_content, new SettingsFragment())
                .commit();
    }
}
