package de.uni_oldenburg.carfinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.PhotoUtils;

public class DetailsFragment extends Fragment {

    private TextView addedTime;
    private TextView notes;
    private ImageView picture;
    private View rootView;
    private ParkingSpot data;
    private TextView parkingMeter;
    private CardView imageDetails;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        this.initUI();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Sets the parking spot data to display
     *
     * @param data
     */
    public void setData(ParkingSpot data) {

        this.data = data;

        displayData();
    }

    public void displayData() {

        Date currentDate = new Date(data.getTimestamp());
        String dateString = new SimpleDateFormat("dd.MM.yy, HH:mm").format(currentDate);

        if (this.data.getExpiresAt() != -1) {
            Date parkingMeter = new Date(this.data.getExpiresAt());
            String parkingMeterString = new SimpleDateFormat("dd.MM.yy, HH:mm").format(parkingMeter);

            this.parkingMeter.setText(getString(R.string.parking_meter_expires) + " " + parkingMeterString);
        } else {
            this.parkingMeter.setText(getString(R.string.parking_meter_not_set));
        }

        this.addedTime.setText(getString(R.string.added_on)+ " " + dateString);
        if (!data.getDescription().equals(getString(R.string.add_note)))
            this.notes.setText(data.getDescription());
        else
            this.notes.setText("-");

        this.picture.post(() -> {
            if (data.getImageLocation() != null) //TODO: check if image exists
                PhotoUtils.loadFileIntoImageView(DetailsFragment.this.picture, data.getImageLocation());
            else {
                imageDetails.setVisibility(View.GONE);
            }
        });


    }


    private void initUI() {
        this.addedTime = rootView.findViewById(R.id.detailsTimeAdded);
        this.picture = rootView.findViewById(R.id.detailsPicture);
        this.notes = rootView.findViewById(R.id.detailsNote);
        this.parkingMeter = rootView.findViewById(R.id.detailsParkingMeter);
        this.imageDetails = rootView.findViewById(R.id.imageDetails);

    }

}
