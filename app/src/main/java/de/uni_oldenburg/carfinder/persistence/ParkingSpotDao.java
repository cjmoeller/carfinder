package de.uni_oldenburg.carfinder.persistence;

import java.util.List;

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
    void insertParkingSpots(ParkingSpot... parkingSpots);

    @Update
    void updateParkingSpots(ParkingSpot... parkingSpots);

    @Delete
    void deleteParkingSpots(ParkingSpot... parkingSpots);

    @Query("select * from parking_spots")
    List<ParkingSpot> loadAllParkingSpots();


}
