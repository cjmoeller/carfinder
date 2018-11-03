package de.uni_oldenburg.carfinder.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.util.PhotoUtils;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;

import static android.app.Activity.RESULT_OK;

public class NewParkingSpotFragment extends Fragment {

    private TextView newAddress;
    private TextView newLatLong;
    private ParkingSpot parkingSpot;
    private Button startParkingButton;
    private CardView photoCard;
    private CardView notesCard;
    private CardView clockCard;
    private ImageView pictureImageView;

    private MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        parkingSpot = this.viewModel.getParkingSpot();

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

        });

        pictureImageView = getActivity().findViewById(R.id.imageViewPicture);

        photoCard = getActivity().findViewById(R.id.createPicture);
        notesCard = getActivity().findViewById(R.id.createNote);
        clockCard = getActivity().findViewById(R.id.createClock);

        photoCard.setClickable(true);
        photoCard.setOnClickListener(v -> startCameraForPicture());

        notesCard.setClickable(true);
        notesCard.setOnClickListener(v -> addNote());

        clockCard.setClickable(true);
        clockCard.setOnClickListener(v -> addClock());

        newAddress.setText(this.parkingSpot.getAddress());
        newLatLong.setText(this.parkingSpot.getLatitude() + ", " + this.parkingSpot.getLongitude());
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
