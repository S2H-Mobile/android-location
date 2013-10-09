package de.s2hmobile.carlib.geocoding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;

public class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {

	/**
	 * Functional interface, provides callback for when the address is updated.
	 * 
	 * @author Stephan Hoehne
	 * 
	 */
	public interface OnAddressUpdatedListener {

		/**
		 * Triggered when the address is updated.
		 * 
		 * @param address
		 *            - the result of the query
		 */
		public void onAddressUpdated(final String address);
	}

	/** Define the maximum number of results for a query. */
	private static final int MAX_RESULTS = 1;

	private final OnAddressUpdatedListener mCallback;

	private final Context mContext;

	private ReverseGeocodingTask(final OnAddressUpdatedListener listener,
			final Context context) {
		mCallback = listener;
		mContext = context;
	}

	@Override
	protected String doInBackground(final Double... params) {

		// get the parameters
		final double lat = params[0];
		final double lng = params[1];

		final Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

		try {
			final List<Address> addresses = geocoder.getFromLocation(lat, lng,
					MAX_RESULTS);

			String result = null;
			if (addresses != null && addresses.size() > 0) {
				final Address address = addresses.get(0);

				// get the first line of address (if available)
				final String street = address.getMaxAddressLineIndex() > 0 ? address
						.getAddressLine(0) : new String();
				final String city = address.getLocality();
				result = String.format("%s, %s", street, city);
			}
			return result;
		} catch (final IOException e) {
			return null;
		}
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

	/**
	 * Starts an async task to reverse geocode the address from the given
	 * coordinates.
	 * 
	 * @param listener
	 *            - the callback
	 * @param context
	 *            - the activity context
	 * @param coordinates
	 *            - latitude and longitude
	 * @return True if task has been started successfully.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static final boolean startGeocodingTask(
			final OnAddressUpdatedListener listener, final Context context,
			final Double[] coordinates) {
		if (Geocoder.isPresent()) {
			final ReverseGeocodingTask task = new ReverseGeocodingTask(
					listener, context);
			task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, coordinates);
			return true;
		} else {
			return false;
		}
	}
}