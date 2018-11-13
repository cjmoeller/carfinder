package de.uni_oldenburg.carfinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;
import de.uni_oldenburg.carfinder.R;
import de.uni_oldenburg.carfinder.activities.HistoryActivity;
import de.uni_oldenburg.carfinder.persistence.ParkingSpot;
import de.uni_oldenburg.carfinder.persistence.ParkingSpotDatabaseManager;
import de.uni_oldenburg.carfinder.viewmodels.HistoryViewModel;

public class HistoryListFragment extends ListFragment {

    private HistoryViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(HistoryViewModel.class);
        ParkingSpotDatabaseManager.getAllParkingSpots(getContext(), data -> onDataLoaded(data));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private List<ParkingSpot> data;

    private Void onDataLoaded(List<ParkingSpot> data) { //TODO: Sort list by date.
        this.data = data;
        this.getListView().setDivider(null); //removes the divider
        List<HashMap<String, String>> aList = new ArrayList<>();

        for (ParkingSpot parkingSpot : data) {
            HashMap<String, String> hm = new HashMap<String, String>();
            Date currentDate = new Date(parkingSpot.getTimestamp());
            String dateString = new SimpleDateFormat("dd.MM.yy").format(currentDate);

            hm.put("date", dateString);
            hm.put("title", parkingSpot.getName());
            hm.put("address", parkingSpot.getAddress());


            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {"title", "address", "date"};

        // Ids of views in listview_layout
        int[] to = {R.id.history_title, R.id.history_address, R.id.historyDate};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listitem_history, from, to);

        setListAdapter(adapter);

        if(this.viewModel.getSelectedParkingSpot() != null){
            //TODO: Perform click on that spot.
        }
        return null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (this.data != null) {
            ParkingSpot selectedItem = this.data.get(position);
            if (this.getActivity() instanceof HistoryActivity) {
                ((HistoryActivity) this.getActivity()).onParkingSpotSelected(selectedItem);
                this.viewModel.setSelectedParkingSpot(selectedItem);
            }
            for (int i = 0; i < this.getListView().getChildCount(); i++) {
                View currentView = this.getListView().getChildAt(i);
                if (i == position) {
                    currentView.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorHistorySelected));
                } else {
                    currentView.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorHistoryNotSelected));

                }
            }
        }
    }
}
