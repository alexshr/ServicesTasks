package com.otus.alexshr.tasks.geo;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.otus.alexshr.tasks.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * AsyncTask for reverse geocoding coordinates into a physical address.
 */
public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private Context mContext;
    private OnTaskCompleted mListener;

    FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Location... params) {
        Geocoder geocoder = new Geocoder(mContext,
                Locale.getDefault());

        Location location = params[0];
        List<Address> addresses = null;

        ArrayList<String> resultParts = new ArrayList<>();
        resultParts.add(mContext.getString(R.string.location, location.getLatitude(), location.getLongitude()));

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address
                    1);

            if (addresses == null || addresses.isEmpty()) {
                resultParts.add(mContext.getString(R.string.no_address_found));
            } else {
                Address address = addresses.get(0);

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    resultParts.add(address.getAddressLine(i));
                }
            }
        } catch (IOException ioException) {
            resultParts.add(mContext.getString(R.string.service_not_available));
            Timber.e(ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            resultParts.add(mContext.getString(R.string.invalid_lat_long_used));
            Timber.e(illegalArgumentException);
        }

        String result = TextUtils.join(
                "\n",
                resultParts);

        Timber.d("result message %s", result);
        return result;
    }

    /**
     * Called once the background thread is finished and updates the
     * UI with the result.
     *
     * @param address The resulting reverse geocoded address, or error
     *                message if the task failed.
     */
    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }
}
