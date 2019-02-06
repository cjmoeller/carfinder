package de.uni_oldenburg.carfinder.location.geocoding;

import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.arch.core.util.Function;
import de.uni_oldenburg.carfinder.util.Constants;

/**
 * Helper-Klasse, die als Callback für eine Adressanfrage dient.
 */
public class AddressStringResultReceiver extends ResultReceiver {

    private Function<String, Void> callback;

    public AddressStringResultReceiver(Function<String, Void> callback) {
        super(null);
        this.callback = callback;

    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle bundle) {
        if (resultCode == Constants.FAILURE_RESULT) {
            this.callback.apply(null);
        } else if (resultCode == Constants.SUCCESS_RESULT) {
            String result = bundle.getString(Constants.ADDRESS_STRING_EXTRA);
            this.callback.apply(result);
        }
    }
}
