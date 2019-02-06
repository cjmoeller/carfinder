package de.uni_oldenburg.carfinder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.MainActivity;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;

/**
 * ExistingParkingSpotFragment: Erweitert das DetailsFragments um eine Anzeige von Adresse, Navigations und Löschen-Button
 */
public class ExistingParkingSpotFragment extends Fragment {

    private DetailsFragment details;

    private TextView existingName;
    private TextView existingAddress;
    private MainViewModel viewModel;
    private Button startNavigation;
    private Button deleteSpot;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        return inflater.inflate(R.layout.fragment_existing, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeUI();
    }


    private void initializeUI() {
        List<Fragment> children = getChildFragmentManager().getFragments();
        for (Fragment f : children) {
            if (f instanceof DetailsFragment) {
                details = (DetailsFragment) f;
            }
        }

        details.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            public void detailsFragmentStarted() {
                details.setData(ExistingParkingSpotFragment.this.viewModel.getParkingSpot());

            }
        });


        LinearLayout bottomSheetLayout = getActivity().findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        existingName = getActivity().findViewById(R.id.existingName);
        existingAddress = getActivity().findViewById(R.id.existingAddress);

        deleteSpot = getActivity().findViewById(R.id.deleteSpot);
        deleteSpot.setOnClickListener(v -> {
            viewModel.setParkingSpotSaved(false);
            ParkingSpotDatabaseManager.archiveParkingSpot(this.viewModel.getParkingSpot().clone(), getContext()); //Cloning needed for async process.
            viewModel.getParkingSpot().setImageLocation(null);
            viewModel.getParkingSpot().setExpiresAt(-1);
            Activity parentActivity = getActivity();
            if (parentActivity instanceof MainActivity) {
                ((MainActivity) parentActivity).loadNewParkingSpotFragment();
                ((MainActivity) parentActivity).displayLocation();
            }
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        startNavigation = getActivity().findViewById(R.id.startNavigation);
        startNavigation.setOnClickListener(v -> {
            //Fußgänger-Navigations-Intent
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + this.viewModel.getParkingSpot().getLatitude() + "," + this.viewModel.getParkingSpot().getLongitude() + "&mode=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        existingName.setText(this.viewModel.getParkingSpot().getName());
        existingAddress.setText(this.viewModel.getParkingSpot().getAddress());
    }
}
