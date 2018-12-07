package de.uni_oldenburg.carfinder.wearable;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.wear.activity.ConfirmationActivity;

public class MainActivity extends WearableActivity {

    private ImageButton parkHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parkHere = findViewById(R.id.button);

        parkHere.setOnClickListener(v -> {

            //Broadcast to all nodes:
            (new Thread() {
                @Override
                public void run() {
                    try {
                        List<Node> nodes = Tasks.await(Wearable.getNodeClient(MainActivity.this).getConnectedNodes());
                        for (Node node : nodes) {
                            Task<Integer> sendTask =
                                    Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), "/park", "parkhere".getBytes());

                            sendTask.addOnSuccessListener(dataItem -> {
                                //Show confirmation
                                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                        ConfirmationActivity.SUCCESS_ANIMATION);
                                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                                        "Geparkt!");
                                startActivity(intent);

                                MainActivity.this.finish();
                            });
                            sendTask.addOnFailureListener(e -> {
                                Log.d("def", "Failed: " + e.getLocalizedMessage());
                                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                        ConfirmationActivity.FAILURE_ANIMATION);
                                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                                        "Geparkt!");
                                startActivity(intent);
                            });
                        }
                    } catch (ExecutionException e) {
                        Log.e("def", e.getLocalizedMessage());
                    } catch (InterruptedException e) {
                        Log.e("def", e.getLocalizedMessage());
                    }
                }
            }).start();

        });

        // Enables Always-on
        setAmbientEnabled();
    }
}
