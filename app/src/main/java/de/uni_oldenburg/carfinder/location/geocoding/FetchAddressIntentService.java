package de.uni_oldenburg.carfinder.location.geocoding;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import de.uni_oldenburg.carfinder.util.Constants;

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver resultReceiver;


    /**
     * Creates an FetchAddressIntentService.
     */
    public FetchAddressIntentService() {
        super("AddressFetcherService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (intent == null) {
            return;
        }
        String errorMessage = "";

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        resultReceiver = intent.getParcelableExtra(Constants.ADDRESS_RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            Log.e(Constants.LOG_TAG, "IO Exception", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e(Constants.LOG_TAG, "Wrong Lat/Lon" + ". " + "Latitude = " + location.getLatitude() + "Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                Log.e(Constants.LOG_TAG, "No Address Found");
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            String addressString = address.getThoroughfare() + " " + address.getSubThoroughfare() + ", " + address.getPostalCode() + " " + address.getLocality();


            Log.i(Constants.LOG_TAG, "Found Address");
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressString);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ADDRESS_STRING_EXTRA, message);
        resultReceiver.send(resultCode, bundle);
    }
}
