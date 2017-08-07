/*
 * Copyright (C) 2012 - 2014, S2H Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.s2hmobile.location;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.text.format.DateUtils;

/**
 * Contains static helper methods to determine location.
 * 
 * @author Stephan Hoehne
 */
public final class LocationHelper {

	/**
	 * Defines a listener for location updates.
	 * 
	 * @author Stephan Hoehne
	 */
	public interface OnLocationUpdateListener {

		/**
		 * Handle the result of the location update.
		 * 
		 * @param location
		 *            - the new location
		 */
		void onLocationUpdate(final Location location);
	}

	/** Allowed accuracy drop of a new location. */
	public static final float ALLOWED_ACCURACY_DELTA = 30.0F; // 30 meters

	/** Maximum age in milliseconds for a location to be considered recent. */
	public static final long DEFAULT_TIME_LIMIT = 3 * DateUtils.MINUTE_IN_MILLIS;

	private LocationHelper() {
	}

	/**
	 * Find the most accurate and timely previously detected location using all
	 * the location providers. This method is derived from the Location Best
	 * Practises project by Reto Meier.
	 * 
	 * @param context
	 *            the context, to get the location manager
	 * @param minTime
	 *            the time limit
	 * @return The most accurate and / or timely previously detected location.
	 */
	public static Location getLastBestLocation(final Context context,
			final long minTime) {
		Location bestLocation = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;
		final LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		final List<String> matchingProviders = locationManager
				.getAllProviders();
		for (final String provider : matchingProviders) {
			final Location location = locationManager
					.getLastKnownLocation(provider);
			if (location != null) {
				final float accuracy = location.getAccuracy();
				final long time = location.getTime();
				if (minTime < time && accuracy < bestAccuracy) {

					/*
					 * This location fix is younger than minTime, its accuracy
					 * is better than the current best value.
					 */
					bestLocation = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time < minTime && bestAccuracy == Float.MAX_VALUE
						&& bestTime < time) {

					/*
					 * First condition not met, since candidate is older than
					 * minTime but younger than current bestResult.
					 */
					bestLocation = location;

					/*
					 * Accuracy not updated, so the condition can be met by the
					 * next candidate.
					 */
					bestTime = time;
				}
			}
		}
		return bestLocation;
	}

	/**
	 * Checks if the caller implements the OnLocationListener interface. Asks
	 * for the last best location. If it is not good enough, requests a single
	 * update.
	 * 
	 * @param context
	 *            - for the location system service
	 * @param listener
	 *            - callback for location update
	 */
	public static void requestLocation(final Context context,
			final OnLocationUpdateListener listener) {

		// set the time limit
		final long limit = System.currentTimeMillis() - DEFAULT_TIME_LIMIT;

		// ask for last best location
		final Location lastBestLocation = getLastBestLocation(context, limit);

		// evaluate the result
		if (LocationHelper.isLocationAccepted(lastBestLocation, limit)) {

			listener.onLocationUpdate(lastBestLocation);
		} else {

			// trigger one-shot update
			final ILocationFinder finder = LocationHelper.createInstance(
					context, listener);
			finder.oneShotUpdate(lastBestLocation);
		}
	}

	/**
	 * Factory that returns a location finder instance, depending on platform
	 * version.
	 * 
	 * @param context
	 *            - for the location system service
	 * @param listener
	 *            - the listener to return the result to
	 * @return The location finder instance.
	 */
	private static ILocationFinder createInstance(final Context context,
			final OnLocationUpdateListener listener) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD ? new GingerbreadLocationFinder(
				context, listener) : new FroyoLocationFinder(context, listener);
	}

	/**
	 * Evaluates the quality of the location based on its age and accuracy. The
	 * criteria are defined as constants. The time limit should be determined
	 * using the basic equation limit = now - age.
	 * 
	 * @param location
	 *            - the location to evaluate
	 * @param limit
	 *            - the maximum age of the location fix
	 * @return True if location meets criteria.
	 */
	private static boolean isLocationAccepted(final Location location,
			final long limit) {
		if (location == null) {
			return false;
		}

		/*
		 * Location.getTime() and System.currentTimeMillis() both use UTC time
		 * which can jump. For API Level 17, use
		 * Location.getElapsedRealtimeNanos() and System.elapsedRealtimeNanos().
		 */
		final long time = location.getTime();
		final float accuracy = location.getAccuracy();
		return limit < time && accuracy < ALLOWED_ACCURACY_DELTA;
	}
}