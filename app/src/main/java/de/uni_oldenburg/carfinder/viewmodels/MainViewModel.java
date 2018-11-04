package de.uni_oldenburg.carfinder.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

public class MainViewModel extends ViewModel {

    private boolean checkedDatabase;
    private ParkingSpot parkingSpot;
    private boolean parkingSpotSaved;

    private MutableLiveData<String> currentPositionAddress;
    private MutableLiveData<Double> currentPositionLat;
    private MutableLiveData<Double> currentPositionLon;


    public MainViewModel() {
        parkingSpot = new ParkingSpot(0, "empty", "empty", null, false, -1, 0, 0, "empty");
        checkedDatabase = false;
        parkingSpotSaved = false;
        currentPositionAddress = new MutableLiveData<>();
        currentPositionLat = new MutableLiveData<>();
        currentPositionLon = new MutableLiveData<>();
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

    public MutableLiveData<String> getCurrentPositionAddress() {
        return currentPositionAddress;
    }

    public MutableLiveData<Double> getCurrentPositionLat() {
        return currentPositionLat;
    }

    public MutableLiveData<Double> getCurrentPositionLon() {
        return currentPositionLon;
    }
}
