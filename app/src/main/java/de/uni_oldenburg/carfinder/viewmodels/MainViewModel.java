package de.uni_oldenburg.carfinder.viewmodels;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

public class MainViewModel extends ViewModel {

    private boolean checkedDatabase;
    private ParkingSpot parkingSpot;
    private boolean parkingSpotSaved;

    private List<Marker> publicParkingSpots;

    private MutableLiveData<String> currentPositionAddress;
    private MutableLiveData<Double> currentPositionLat;
    private MutableLiveData<Double> currentPositionLon;

    //MainActivity States
    private MutableLiveData<Boolean> isMapLoaded;
    private MutableLiveData<Boolean> isDatabaseLoaded;
    private MutableLiveData<Boolean> isPositionReady;
    private MutableLiveData<Boolean> updatingPosition;

    //merges all into one ready state:
    private MediatorLiveData<Boolean> isLoadingDone;

    public MainViewModel() {
        parkingSpot = new ParkingSpot(0, "empty", "empty", null, false, -1, 0, 0, "empty");
        checkedDatabase = false;
        parkingSpotSaved = false;
        updatingPosition = new MutableLiveData<>();
        currentPositionAddress = new MutableLiveData<>();
        currentPositionLat = new MutableLiveData<>();
        currentPositionLon = new MutableLiveData<>();

        isDatabaseLoaded = new MutableLiveData<>();
        isMapLoaded = new MutableLiveData<>();
        isPositionReady = new MutableLiveData<>();

        isLoadingDone = new MediatorLiveData<>();
        isLoadingDone.addSource(isDatabaseLoaded, value -> isLoadingDone.setValue(value && this.isPositionReady.getValue() && this.isMapLoaded.getValue()));
        isLoadingDone.addSource(isPositionReady, value -> isLoadingDone.setValue(value && this.isDatabaseLoaded.getValue() && this.isMapLoaded.getValue()));
        isLoadingDone.addSource(isMapLoaded, value -> isLoadingDone.setValue(value && this.isPositionReady.getValue() && this.isDatabaseLoaded.getValue()));

        publicParkingSpots = new ArrayList<>();

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

    public MutableLiveData<Boolean> getIsMapLoaded() {
        return isMapLoaded;
    }


    public MutableLiveData<Boolean> getIsDatabaseLoaded() {
        return isDatabaseLoaded;
    }


    public MutableLiveData<Boolean> getIsPositionReady() {
        return isPositionReady;
    }

    public MediatorLiveData<Boolean> getIsLoadingDone() {
        return isLoadingDone;
    }

    public MutableLiveData<Boolean> getUpdatingPosition() {
        return updatingPosition;
    }

    public List<Marker> getPublicParkingSpots() {
        return publicParkingSpots;
    }
}
