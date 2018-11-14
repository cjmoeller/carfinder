package de.uni_oldenburg.carfinder.viewmodels;

import androidx.lifecycle.ViewModel;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

/**
 * ViewModel for the DetailsFragement
 */
public class HistoryViewModel extends ViewModel {
    private ParkingSpot selectedParkingSpot;
    private boolean masterDetailMode;

    public HistoryViewModel(){
        this.setMasterDetailMode(false);
    }

    public ParkingSpot getSelectedParkingSpot() {
        return selectedParkingSpot;
    }

    public void setSelectedParkingSpot(ParkingSpot selectedParkingSpot) {
        this.selectedParkingSpot = selectedParkingSpot;
    }

    public boolean isMasterDetailMode() {
        return masterDetailMode;
    }

    public void setMasterDetailMode(boolean masterDetailMode) {
        this.masterDetailMode = masterDetailMode;
    }
}
