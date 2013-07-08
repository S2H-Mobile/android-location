package de.s2hmobile.carlib.geocoding;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {
	private static final int MAX_RESULTS = 1;

	private OnAddressUpdatedListener mCallback = null;

	private final Context mContext;

	public ReverseGeocodingTask(Activity activity, Context context) {
		try {
			mCallback = (OnAddressUpdatedListener) activity;
		} catch (ClassCastException e) {
			android.util.Log.e("ReverseGeocodingTask",
					"Caller must implement OnAddressEncodedListener.", e);
		}
		mContext = context;
	}

	@Override
	protected String doInBackground(Double... params) {
		String result = null;
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		List<Address> addresses = null;
		double lat = params[0], lng = params[1];
		try {
			addresses = geocoder.getFromLocation(lat, lng, MAX_RESULTS);
		} catch (java.io.IOException e) {
		}
		if (addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);
			// get the first line of address (if available)
			String street = address.getMaxAddressLineIndex() > 0 ? address
					.getAddressLine(0) : "";
			String city = address.getLocality();
			result = String.format("%s, %s", street, city);
		}
		return result;
	}

	@Override
	protected void onPostExecute(String address) {
		if (super.isCancelled()) {
			address = null;
		}
		if (address != null && mCallback != null) {
			mCallback.onAddressUpdated(address);
		}
	}

	
}