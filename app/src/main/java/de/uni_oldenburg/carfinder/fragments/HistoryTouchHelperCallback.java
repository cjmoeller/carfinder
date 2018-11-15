package de.uni_oldenburg.carfinder.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.RecyclerView;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;

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
        }
    }
}
