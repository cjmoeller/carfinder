package de.uni_oldenburg.carfinder.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import de.uni_oldenburg.carfinder.R;

/**
 * Zeigt einen 'Alarm abgelaufen' Dialog.
 */
public class AlarmExpiredDialogFragment extends DialogFragment {

   public AlarmExpiredDialogFragment() {
        // Empty constructor is required for DialogFragment
   }

    public static AlarmExpiredDialogFragment newInstance(String title) {
        AlarmExpiredDialogFragment frag = new AlarmExpiredDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.dialog_meter_expired, container);
        v.setPadding(20,20,20,20);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       Button okay = view.findViewById(R.id.expired_ok_button);
       okay.setOnClickListener(v -> this.dismiss());
    }
}