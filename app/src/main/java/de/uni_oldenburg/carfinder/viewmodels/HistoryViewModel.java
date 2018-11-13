package de.uni_oldenburg.carfinder.viewmodels;

import androidx.lifecycle.ViewModel;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

/**
 * ViewModel for the DetailsFragement
 */
public class HistoryViewModel extends ViewModel {
    private ParkingSpot selectedParkingSpot;

    public ParkingSpot getSelectedParkingSpot() {
        return selectedParkingSpot;
    }

    public void setSelectedParkingSpot(ParkingSpot selectedParkingSpot) {
        this.selectedParkingSpot = selectedParkingSpot;
    }
}
