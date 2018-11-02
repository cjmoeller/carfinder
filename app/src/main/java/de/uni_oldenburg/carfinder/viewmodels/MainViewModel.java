package de.uni_oldenburg.carfinder.viewmodels;

import androidx.lifecycle.ViewModel;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

public class MainViewModel extends ViewModel {

    private boolean checkedDatabase;
    private ParkingSpot parkingSpot;
    private boolean parkingSpotSaved;

    public MainViewModel() {
        parkingSpot = new ParkingSpot(0, "empty", "empty", null, false, -1, 0, 0, "empty");
        checkedDatabase = false;
        parkingSpotSaved = false;
    }



    public boolean alreadyCheckedDatabase() {
        return checkedDatabase;
    }

    public void setCheckedDatabase(boolean checkedDatabase) {
        this.checkedDatabase = checkedDatabase;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public boolean isParkingSpotSaved() {
        return parkingSpotSaved;
    }

    public void setParkingSpotSaved(boolean parkingSpotSaved) {
        this.parkingSpotSaved = parkingSpotSaved;
    }
}
