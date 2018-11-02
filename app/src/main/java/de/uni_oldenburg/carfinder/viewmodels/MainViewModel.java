package de.uni_oldenburg.carfinder.viewmodels;

import androidx.lifecycle.ViewModel;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

public class MainViewModel extends ViewModel {

    private boolean checkedDatabase;
    private ParkingSpot currentSpot;

    public MainViewModel(){
        currentSpot = null;
        checkedDatabase = false;
    }

    public boolean alreadyCheckedDatabase() {
        return checkedDatabase;
    }

    public void setCheckedDatabase(boolean checkedDatabase) {
        this.checkedDatabase = checkedDatabase;
    }

    public ParkingSpot getCurrentSpot() {
        return currentSpot;
    }

    public void setCurrentSpot(ParkingSpot currentSpot) {
        this.currentSpot = currentSpot;
    }
}
