package de.uni_oldenburg.carfinder.persistence;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import androidx.arch.core.util.Function;
import androidx.room.Room;
import de.uni_oldenburg.carfinder.util.Constants;

public class ParkingSpotDatabaseManager {
    private static AppDatabase instance;

    private ParkingSpotDatabaseManager() {
    }

    public static AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    AppDatabase.class, Constants.DATABASE_NAME).build();
        }
        return instance;
    }

    public static void insertParkingSpot(final ParkingSpot parkingSpot, final Context context) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ParkingSpotDatabaseManager.getDatabase(context).parkingSpotDao().insertParkingSpots(parkingSpot);
                return null;
            }
        };
        task.execute();
    }

    public static void getAllParkingSpots(final Context context, Function<List<ParkingSpot>, Void> callback) {
        LoadParkingSpotsTask task = new LoadParkingSpotsTask(context, callback);
        task.execute();
    }
}
