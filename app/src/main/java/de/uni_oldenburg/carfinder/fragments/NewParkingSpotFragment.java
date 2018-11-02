package de.uni_oldenburg.carfinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.Constants;

public class NewParkingSpotFragment extends Fragment {

    private TextView newAddress;
    private TextView newLatLong;
    private ParkingSpot parkingSpot;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        parkingSpot = (ParkingSpot) args.getSerializable(Constants.PARKING_SPOT_OBJECT_BUNDLE);

        return inflater.inflate(R.layout.fragment_new, container, false);
    }

    @Override
    public void onStart() {

        initializeUI();
        super.onStart();
    }

    private void initializeUI() {
        newAddress = getActivity().findViewById(R.id.newAddress);
        newLatLong = getActivity().findViewById(R.id.newLatLon);

        newAddress.setText(this.parkingSpot.getAddress());
        newLatLong.setText(this.parkingSpot.getLatitude() + ", " + this.parkingSpot.getLongitude());
    }


}
