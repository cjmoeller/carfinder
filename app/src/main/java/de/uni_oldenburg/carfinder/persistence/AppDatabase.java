package de.uni_oldenburg.carfinder.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ParkingSpot.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ParkingSpotDao parkingSpotDao();
}