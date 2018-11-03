package de.uni_oldenburg.carfinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;

public class ExistingParkingSpotFragment extends Fragment {

    private DetailsFragment details;

    private ParkingSpot parkingSpot;

    private TextView existingName;
    private TextView existingAddress;
    private MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        parkingSpot = viewModel.getParkingSpot();

        return inflater.inflate(R.layout.fragment_existing, container, false);
    }

    @Override
    public void onStart() {

        initializeUI();
        super.onStart();
    }

    private void initializeUI() {
        List<Fragment> children = getChildFragmentManager().getFragments();
        for (Fragment f : children) {
            if (f instanceof DetailsFragment) {
                details = (DetailsFragment) f;
            }
        }

        existingName = getActivity().findViewById(R.id.existingName);
        existingAddress = getActivity().findViewById(R.id.existingAddress);
        existingName.setText(parkingSpot.getName());
        existingAddress.setText(parkingSpot.getAddress());
    }
}
