package de.uni_oldenburg.carfinder.persistence;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * DAO for parking Spots.
 */
@Dao
public interface ParkingSpotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertParkingSpot(ParkingSpot parkingSpot);

    @Update
    void updateParkingSpots(ParkingSpot... parkingSpots);

    @Delete
    void deleteParkingSpots(ParkingSpot... parkingSpots);

    @Query("update parking_spots set isCurrentlyUsed = 0")
    void archiveAllSpots();

    @Query("select * from parking_spots")
    List<ParkingSpot> loadAllParkingSpots();

    @Query("select * from parking_spots where id = :pid")
    LiveData<ParkingSpot> getParkingSpotById(long pid);


}
