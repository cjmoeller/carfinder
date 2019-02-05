package de.uni_oldenburg.carfinder.persistence;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
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

    public static void insertParkingSpot(final ParkingSpot parkingSpot, final Function<Long, Void> callback, final Context context) {
        AsyncTask task = new AsyncTask<Object, Object, Long>() {
            @Override
            protected Long doInBackground(Object[] objects) {
                ParkingSpotDatabaseManager.getDatabase(context).parkingSpotDao().archiveAllSpots();
                return ParkingSpotDatabaseManager.getDatabase(context).parkingSpotDao().insertParkingSpot(parkingSpot);

            }

            @Override
            protected void onPostExecute(Long result) {
                callback.apply(result);
            }
        };
        task.execute();
    }

    public static void archiveParkingSpot(final ParkingSpot parkingSpot, final Context context) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                parkingSpot.setCurrentlyUsed(false);
                ParkingSpotDatabaseManager.getDatabase(context).parkingSpotDao().updateParkingSpots(parkingSpot);
                return null;
            }
        };
        task.execute();
    }

    public static void deleteParkingSpot(final ParkingSpot parkingSpot, final Context context) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ParkingSpotDatabaseManager.getDatabase(context).parkingSpotDao().deleteParkingSpots(parkingSpot);
                return null;
            }
        };
        task.execute();
    }

    public static void getAllParkingSpots(final Context context, Function<List<ParkingSpot>, Void> callback) {
        LoadParkingSpotsTask task = new LoadParkingSpotsTask(context, callback);
        task.execute();
    }

    public static LiveData<ParkingSpot> getParkingSpot(long id, Context ctx){
        return ParkingSpotDatabaseManager.getDatabase(ctx).parkingSpotDao().getParkingSpotById(id);
    }
}
