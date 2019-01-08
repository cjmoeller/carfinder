package de.uni_oldenburg.carfinder.viewmodels;

import androidx.lifecycle.ViewModel;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

public class DetailsViewModel extends ViewModel {
    public ParkingSpot getData() {
        return data;
    }

    public void setData(ParkingSpot data) {
        this.data = data;
    }

    private ParkingSpot data;
}
