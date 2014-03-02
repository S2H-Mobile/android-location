package de.s2hmobile.carlib.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.text.format.DateUtils;
import de.s2hmobile.carlib.location.LocationHelper.OnLocationUpdateListener;

abstract class LocationFinderBase implements ILocationFinder {

	protected Location mCurrentLocation = null;

	protected final OnLocationUpdateListener mCallback;

	protected final Context mContext;

	protected final LocationManager mLocationManager;

	protected LocationFinderBase(final Context context,
			final OnLocationUpdateListener callback) {
		mContext = context;
		mCallback = callback;
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	/**
	 * Compares the new location to the current best locations. Returns the
	 * "better" location. The allowed time delta is set to two minutes.
	 * 
	 * @param newLocation
	 *            - the new location to evaluate
	 * @param currentBestLocation
	 *            - the current best fix
	 */
	protected static Location betterLocation(final Location newLocation,
			final Location currentBestLocation) {
		if (currentBestLocation == null) {

			// a new location is always better than no location
			return newLocation;
		} else if (newLocation == null) {

			return currentBestLocation;
		} else {

			// compare the times of the location fixes
			final long allowedTimeDelta = 2 * DateUtils.MINUTE_IN_MILLIS;
			final long timeDelta = newLocation.getTime()
					- currentBestLocation.getTime();

			if (timeDelta > allowedTimeDelta) {

				// newLocation is more than two minutes younger than current
				// one, user has likely moved
				return newLocation;
			} else if (timeDelta < -allowedTimeDelta) {

				// newLocation is more than two minutes older than current one
				return currentBestLocation;
			}

			// the location times are less than two minutes apart, compare the
			// accuracies
			final float accuracyDelta = newLocation.getAccuracy()
					- currentBestLocation.getAccuracy();
			if (accuracyDelta < 0) {

				// the new location fix is more accurate than the current best
				// fix
				return newLocation;
			} else if (timeDelta > 0
					&& !(accuracyDelta > LocationHelper.ALLOWED_ACCURACY_DELTA)) {

				// candidate location is younger and not significantly less
				// accurate
				return newLocation;
			}

			// candidate location is older or significantly less accurate
			return currentBestLocation;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void oneShotUpdate(final Location currentBestLocation) {

		// save the current location for the receiver to compare to the new one
		mCurrentLocation = currentBestLocation;

		// define the criteria for the location update
		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		invokeBroadcast(criteria);
	}

	protected abstract void invokeBroadcast(final Criteria criteria);
}