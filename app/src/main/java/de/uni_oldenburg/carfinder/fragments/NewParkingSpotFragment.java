package de.uni_oldenburg.carfinder.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.MainActivity;
import de.uni_oldenburg.carfinder.location.TimePickerLocationService;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.util.AlarmReceiver;
import de.uni_oldenburg.carfinder.util.Constants;
import de.uni_oldenburg.carfinder.util.PhotoUtils;
import de.uni_oldenburg.carfinder.viewmodels.MainViewModel;

public class NewParkingSpotFragment extends Fragment {

    private TextView newAddress;
    private TextView newLatLong;
    private TextView notes;
    private TextView clockTextView;
    private Button startParkingButton;
    private CardView photoCard;
    private CardView notesCard;
    private CardView clockCard;
    private ImageView pictureImageView;
    private ImageView notesImageView;
    private ImageView clockImageView;
    private EditText parkingSpotName;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;


    private MainViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        this.viewModel.getParkingSpot().setId(0); //Needed to tell room that it should auto generate an Id for a new spot TODO: Do that as a callback of "Delete Parking Spot"

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
        notes = getActivity().findViewById(R.id.textViewNoteNew);

        startParkingButton = getActivity().findViewById(R.id.startParkingButton);
        startParkingButton.setOnClickListener(v -> {
            if (this.bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                parkingSpotName.requestFocus();
                parkingSpotName.setSelection(parkingSpotName.getText().length());
            } else {
                this.viewModel.getParkingSpot().setCurrentlyUsed(true);
                this.viewModel.getParkingSpot().setName(this.parkingSpotName.getText().toString());
                this.viewModel.getParkingSpot().setDescription(this.notes.getText().toString());
                this.viewModel.getParkingSpot().setTimestamp(System.currentTimeMillis());
                ParkingSpotDatabaseManager.insertParkingSpot(this.viewModel.getParkingSpot(), (Long id) -> {
                    this.viewModel.getParkingSpot().setId(id);
                    return null;
                }, getActivity());
                Activity parentActivity = getActivity();
                if (parentActivity instanceof MainActivity) {
                    ((MainActivity) parentActivity).loadExistingParkingSpotFragment();
                }
                this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            long millisAlarm = viewModel.getParkingSpot().getExpiresAt();
            if(millisAlarm != -1){
                Calendar alarm = Calendar.getInstance();
                alarm.setTimeInMillis(millisAlarm);
                setAlarm(alarm);

                //create and start intent for TimePickerLocationService
                Intent intent = new Intent(this.getContext(), TimePickerLocationService.class);
                intent.putExtra("lat", this.viewModel.getParkingSpot().getLatitude());
                intent.putExtra("lon", this.viewModel.getParkingSpot().getLongitude());
                getActivity().startService(intent);
            }

        });

        pictureImageView = getActivity().findViewById(R.id.imageViewPicture);
        notesImageView = getActivity().findViewById(R.id.imageViewNote);
        clockImageView = getActivity().findViewById(R.id.imageViewClock);
        clockTextView = getActivity().findViewById(R.id.textViewClock);

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
        this.viewModel.getCurrentPositionLat().observe(this, positionLatObserver);
        this.viewModel.getCurrentPositionLon().observe(this, positionLonObserver);

        //
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
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
            File photoFile;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setTitle(getString(R.string.dialog_enter_note));

        builder.setPositiveButton(getString(R.string.dialog_ok), (dialog, id) -> {
            //Remove image View
            ViewGroup parent = (ViewGroup) NewParkingSpotFragment.this.notesImageView.getParent();
            if (parent != null) {
                parent.removeView(NewParkingSpotFragment.this.notesImageView);
            }
            //Add Notes Text
            NewParkingSpotFragment.this.viewModel.getParkingSpot().setDescription(input.getText().toString());
            NewParkingSpotFragment.this.notes.setText(input.getText().toString());
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Starts the parking clock configuration
     */
    private void addClock() {
        Calendar cal_now = Calendar.getInstance();
        Calendar calAlarm = (Calendar) cal_now.clone();
        SharedPreferences pref_minutes = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String minutes_string = pref_minutes.getString("pref_key_set_parking_meter", "");
        int minutes_value;
        if(minutes_string != "Auto" || minutes_string != null){
            minutes_value = Integer.parseInt(minutes_string);
        }else{
            minutes_value = 0;
        }
        //Set up TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, hourOfDay, minutes) -> {
            calAlarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calAlarm.set(Calendar.MINUTE, minutes - minutes_value);
            calAlarm.set(Calendar.SECOND, 0);
            calAlarm.set(Calendar.MILLISECOND, 0);


            if (calAlarm.compareTo(cal_now) <= 0) {
                calAlarm.add(Calendar.DATE, 1);
            }
            //Save Time in ViewModel/ParkingSpot
            long expiresAt = calAlarm.getTimeInMillis();
            viewModel.getParkingSpot().setExpiresAt(expiresAt);
            this.clockTextView.setText(getString(R.string.parking_meter_set) + calAlarm.getTime());
        }, 0, 0, true);
        timePickerDialog.show();


    }

    private void setAlarm(Calendar targetCal) {
        Intent intent = new Intent(getActivity().getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getBaseContext(), Constants.ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        this.viewModel.getParkingSpot().setExpiresAt(targetCal.getTimeInMillis());
        Toast.makeText(this.getContext(), "Alarm wurde erstellt!", Toast.LENGTH_LONG).show();
    }




}
