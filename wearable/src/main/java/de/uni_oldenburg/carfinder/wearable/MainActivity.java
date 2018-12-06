package de.uni_oldenburg.carfinder.wearable;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import androidx.wear.activity.ConfirmationActivity;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private ImageButton parkHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text);
        parkHere = findViewById(R.id.button);

        DataClient mDataClient = Wearable.getDataClient(this);

        parkHere.setOnClickListener(v -> {
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/park");
            putDataMapReq.getDataMap().putLong("PARK_HERE", System.currentTimeMillis());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            putDataReq.setUrgent();
            Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);

            putDataTask.addOnSuccessListener(dataItem -> {
                //Show confirmation
                Intent intent = new Intent(this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                        "Geparkt!");
                startActivity(intent);
            });
            this.finish();
        });
        // Enables Always-on
        setAmbientEnabled();
    }
}
