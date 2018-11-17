package de.uni_oldenburg.carfinder.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.util.Constants;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        Preference syncPreference = findPreference("pref_key_sync");
        syncPreference.setOnPreferenceClickListener(preference -> {
            // Choose authentication
            List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    Constants.RC_SIGN_IN);
            return true;
        });

        MultiSelectListPreference bluetoothDevices = (MultiSelectListPreference) findPreference("pref_key_bluetooth_device");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            bluetoothDevices.setEnabled(false);
            return;
        }

        ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());
        //TODO: Check if bluetooth on. When its not ask the user to turn it on.
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            String[] entries = new String[pairedDevices.size()];
            String[] values = new String[pairedDevices.size()];

            for (int i = 0; i < pairedDevices.size(); i++) {
                String deviceName = pairedDevices.get(i).getName();
                String deviceHardwareAddress = pairedDevices.get(i).getAddress(); // MAC address
                entries[i] = deviceName;
                values[i] = deviceHardwareAddress;
            }

            bluetoothDevices.setEntries(entries);
            bluetoothDevices.setEntryValues(values);
        } else {
            bluetoothDevices.setEnabled(false);
            return;
        }
    }
}
