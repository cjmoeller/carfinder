package de.uni_oldenburg.carfinder.fragments;

import androidx.appcompat.widget.TooltipCompat;
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
 * RecyclerView Adapter for the ParkingSpot Object.
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
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    public void removeItemAt(int index) {
        mValues.remove(index);
        this.notifyItemRemoved(index);
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
            TooltipCompat.setTooltipText(view, view.getContext().getString(R.string.swipe_left_to_delete));

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
