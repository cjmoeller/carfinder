package de.uni_oldenburg.carfinder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.MainActivity;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.util.PhotoUtils;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;

import static android.app.Activity.RESULT_OK;

public class NewParkingSpotFragment extends Fragment {

    private TextView newAddress;
    private TextView newLatLong;
    private Button startParkingButton;
    private CardView photoCard;
    private CardView notesCard;
    private CardView clockCard;
    private ImageView pictureImageView;
    private EditText parkingSpotName;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;


    private MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

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

        startParkingButton = getActivity().findViewById(R.id.startParkingButton);
        startParkingButton.setOnClickListener(v -> {
            if (this.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                parkingSpotName.requestFocus();
                parkingSpotName.setSelection(parkingSpotName.getText().length());
            } else {
                this.viewModel.getParkingSpot().setCurrentlyUsed(true);
                this.viewModel.getParkingSpot().setName(this.parkingSpotName.getText().toString());
                this.viewModel.getParkingSpot().setDescription("Hier könnte man implementieren, dass der Benutzer auch eine Beschreibung hinzufügen kann. Kann man machen, muss man aber nicht... Deswegen steht jetzt dieser ultrakrasse Text hier! 42");
                this.viewModel.getParkingSpot().setTimestamp(System.currentTimeMillis());
                ParkingSpotDatabaseManager.insertParkingSpot(this.viewModel.getParkingSpot(), getActivity());
                Activity parentActivity = getActivity();
                if (parentActivity instanceof MainActivity) {
                    ((MainActivity) parentActivity).loadExistingParkingSpotFragment();
                }
                this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

        });

        pictureImageView = getActivity().findViewById(R.id.imageViewPicture);
        LinearLayout bottomSheetLayout = getActivity().findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        parkingSpotName = getActivity().findViewById(R.id.createParkingSpotName);

        photoCard = getActivity().findViewById(R.id.createPicture);
        notesCard = getActivity().findViewById(R.id.createNote);
        clockCard = getActivity().findViewById(R.id.createClock);

        photoCard.setClickable(true);
        photoCard.setOnClickListener(v -> startCameraForPicture());

        notesCard.setClickable(true);
        notesCard.setOnClickListener(v -> addNote());

        clockCard.setClickable(true);
        clockCard.setOnClickListener(v -> addClock());

        newAddress.setText(this.viewModel.getParkingSpot().getAddress());
        newLatLong.setText(this.viewModel.getParkingSpot().getLatitude() + ", " + this.viewModel.getParkingSpot().getLongitude());


        //Live Data Observers
        final Observer<String> addressObserver = address -> this.newAddress.setText(address);
        final Observer<Double> positionLatObserver = newLat -> newLatLong.setText(newLat + ", " + this.viewModel.getParkingSpot().getLongitude());
        final Observer<Double> positionLonObserver = newLon -> newLatLong.setText(this.viewModel.getParkingSpot().getLatitude() + ", " + newLon);

        this.viewModel.getCurrentPositionAddress().observe(this, addressObserver);
        this.viewModel.getMarkerPositionLat().observe(this, positionLatObserver);
        this.viewModel.getMarkerPositionLon().observe(this, positionLonObserver);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            PhotoUtils.loadFileIntoImageView(this.pictureImageView, this.viewModel.getParkingSpot().getImageLocation());
        }
    }

    /**
     * Starts the camera to take a picture.
     */
    private void startCameraForPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = PhotoUtils.createImageFile(getActivity());
                this.viewModel.getParkingSpot().setImageLocation(photoFile.getAbsolutePath());
            } catch (IOException ex) {
                return; //TODO: handle
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), Constants.FILEPROVIDER_AUTHORITY, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Opens a dialog to save a note.
     */
    private void addNote() {

    }

    /**
     * Starts the parking clock configuration
     */
    private void addClock() {

    }


}
