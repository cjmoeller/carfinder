package de.uni_oldenburg.carfinder.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import de.uni_oldenburg.carfinder.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}
