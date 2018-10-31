package de.uni_oldenburg.carfinder.persistence;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import androidx.arch.core.util.Function;

/**
 * Loads parking spot data from the database in background. Then calls a callback function with the result.
 */
public class LoadParkingSpotsTask extends AsyncTask<Void, Void, List<ParkingSpot>> {
    private Function<List<ParkingSpot>, Void> callback;
    private Context context;

    public LoadParkingSpotsTask(Context context, Function<List<ParkingSpot>, Void> callback) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected List<ParkingSpot> doInBackground(Void... voids) {
        return ParkingSpotDatabaseManager.getDatabase(context).parkingSpotDao().loadAllParkingSpots();
    }

    @Override
    protected void onPostExecute(List<ParkingSpot> result) {
        callback.apply(result);
    }
}
