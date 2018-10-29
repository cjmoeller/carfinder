package de.uni_oldenburg.carfinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.ListFragment;
import de.uni_oldenburg.carfinder.R;

public class HistoryListFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Each row in the list stores country name, currency and flag
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 5; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("title", "Parkplatzname");
            hm.put("address", "Adresse 1, 20123 Stadt");
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {"title", "address"};

        // Ids of views in listview_layout
        int[] to = {R.id.history_title, R.id.history_address};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listitem_history, from, to);

        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}