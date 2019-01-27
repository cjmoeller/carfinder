package de.uni_oldenburg.carfinder.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.RecyclerView;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.util.AlarmReceiver;
import de.uni_oldenburg.carfinder.util.Constants;

/**
 * Used to implement delete on swipe.
 */
public class HistoryTouchHelperCallback extends SimpleCallback {

    private Context ctx;
    private HistoryRecyclerViewAdapter adapter;


    public HistoryTouchHelperCallback(Context ctx, HistoryRecyclerViewAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.ctx = ctx;
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof HistoryRecyclerViewAdapter.ViewHolder) {
            ParkingSpotDatabaseManager.deleteParkingSpot(((HistoryRecyclerViewAdapter.ViewHolder) viewHolder).mItem, ctx);
            adapter.removeItemAt(viewHolder.getAdapterPosition());
            cancelAlarm(((HistoryRecyclerViewAdapter.ViewHolder) viewHolder).mItem);
        }
    }


    //if deleted parkingspot has an active alarm, the alarm will be canceled
    public void cancelAlarm(ParkingSpot parkingSpot){
        if(parkingSpot.getExpiresAt() != -1 && parkingSpot.getExpiresAt() >= System.currentTimeMillis()){ //IF timer is set and IF time is not already expired
            //here starts basic alarmmanager-procedure..
            AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(ctx, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, Constants.ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
    }
}
