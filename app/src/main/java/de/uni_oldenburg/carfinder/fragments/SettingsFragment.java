package de.uni_oldenburg.carfinder.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
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

        CheckBoxPreference autoMode = (CheckBoxPreference) findPreference("pref_key_auto_recognition");
        CheckBoxPreference enhancedAutoMode = (CheckBoxPreference) findPreference("pref_key_enhanced_recognition");
        if(!autoMode.isChecked()){
            enhancedAutoMode.setEnabled(false);
        }
        autoMode.setOnPreferenceChangeListener((preference, newValue) -> {
            if (newValue.equals(Boolean.FALSE)) {
                enhancedAutoMode.setEnabled(false);
                return true;
            } else {
                enhancedAutoMode.setEnabled(true);
                return true;
            }

        });
        //Parking Meter Settings
        //set click listener
        CheckBoxPreference parkingMeterCheckbox = (CheckBoxPreference) findPreference("pref_key_parking_meter");
        parkingMeterCheckbox.setDefaultValue(true);
        parkingMeterCheckbox.setChecked(true);
        parkingMeterCheckbox.setOnPreferenceClickListener(preference -> {
                    Preference pref = findPreference("pref_key_set_parking_meter");
                    if (pref.isEnabled()) {
                        pref.setEnabled(false);
                    } else {
                        pref.setEnabled(true);
                    }
                    return true;
                }

        );

        ListPreference earlierAlarm = (ListPreference) findPreference("pref_key_set_parking_meter");

        String[] entries_parking = new String[Constants.PARKING_METER_PREFERENCE_ENTRIES + 1];
        String[] values_parking = new String[Constants.PARKING_METER_PREFERENCE_ENTRIES + 1];
        entries_parking[0] = "Auto";
        values_parking[0] = "Auto";

        for (int i = 1; i < entries_parking.length; i++) {
            int minutes = i * 5;
            entries_parking[i] = minutes + " Minuten";
            values_parking[i] = Integer.toString(minutes);
        }
        earlierAlarm.setEntries(entries_parking);
        earlierAlarm.setEntryValues(values_parking);


        //Bluetooth-Settings
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
