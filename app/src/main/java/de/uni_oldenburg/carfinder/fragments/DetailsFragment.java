package de.uni_oldenburg.carfinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.util.PhotoUtils;

public class DetailsFragment extends Fragment {

    private ParkingSpot data;
    private TextView addedTime;
    private TextView notes;
    private ImageView picture;
    private ScrollView rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ScrollView) inflater.inflate(R.layout.fragment_details, container);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.initUI();
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Sets the parking spot data to display
     *
     * @param data
     */
    public void setData(ParkingSpot data) {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            this.data = data;
            Date currentDate = new Date(data.getTimestamp());
            this.addedTime.setText("Hinzugefügt am: " + currentDate.toString());
            this.notes.setText(data.getDescription());

            this.picture.post(() -> {
                if (data.getImageLocation() != null) //TODO: check if image exists
                    PhotoUtils.loadFileIntoImageView(DetailsFragment.this.picture, data.getImageLocation());
            });


        }
    }

    private void initUI() {
        this.addedTime = rootView.findViewById(R.id.detailsTimeAdded);
        this.picture = rootView.findViewById(R.id.detailsPicture);
        this.notes = rootView.findViewById(R.id.detailsNote);

    }

}
