package de.uni_oldenburg.carfinder.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.fragments.HistoryFragment.OnListFragmentInteractionListener;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ParkingSpot} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    private final List<ParkingSpot> mValues;
    private final OnListFragmentInteractionListener mListener;

    public HistoryRecyclerViewAdapter(List<ParkingSpot> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Date currentDate = new Date(holder.mItem.getTimestamp());
        String dateString = new SimpleDateFormat("dd.MM.yy").format(currentDate);
        holder.historyDate.setText(dateString);
        holder.historyTitle.setText(holder.mItem.getName());
        holder.historyAddress.setText(holder.mItem.getAddress());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView historyTitle;
        public final TextView historyDate;
        public final TextView historyAddress;
        public ParkingSpot mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            historyAddress = view.findViewById(R.id.history_address);
            historyDate = view.findViewById(R.id.historyDate);
            historyTitle = view.findViewById(R.id.history_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getName() + "'";
        }
    }
}
