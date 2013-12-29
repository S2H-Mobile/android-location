/*
 * Copyright (C) 2012 - 2013, S2H Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.s2hmobile.carlib;

import android.content.SharedPreferences;
import android.location.Location;

/**
 * Provides static access to a data file containing a location.
 * 
 * @author Stephan Hoehne
 */
public final class LocationData {

	private static final long DEFAULT_TIME = Long.MIN_VALUE;

	/** Key for saving the address of the car loaction in the data file. */
	private static final String KEY_ADDRESS = "pref_address";

	/** Key for saving the latitude of the car location in the data file. */
	private static final String KEY_LAT = "pref_lat";

	/** Key for saving the longitude of the car location in the data file. */
	private static final String KEY_LNG = "pref_lng";

	/** Key for saving the timestamp in the data file. */
	private static final String KEY_TIME = "pref_time";

	private LocationData() {
	}

	// public static void clearLocationDataFile(final SharedPreferences data) {
	// data.edit().clear().commit();
	// }

	/**
	 * Returns the address of the parking spot, if one has been saved. There can
	 * be a saved location without an address.
	 * 
	 * @param data
	 *            the location data file
	 * @return the saved address, or an empty string
	 */
	public static String getAddress(final SharedPreferences data) {
		return data.getString(LocationData.KEY_ADDRESS, "");
	}

	/**
	 * Reads the location data from the preferences file.
	 * 
	 * @param data
	 *            - the location data file
	 * @return The coordinate array or (0,0) if nothing has been saved.
	 */
	public static Double[] getPosition(final SharedPreferences data) {

		// read coordinates from data file
		final long lat = data.getLong(KEY_LAT, 0L);
		final long lng = data.getLong(KEY_LNG, 0L);

		// convert to double and initialize the array
		final double latitude = Double.longBitsToDouble(lat);
		final double longitude = Double.longBitsToDouble(lng);

		final Double[] coordinates = { latitude, longitude };
		return coordinates;
	}

	public static long getTime(final SharedPreferences data) {
		return data.getLong(KEY_TIME, DEFAULT_TIME);
	}

	/**
	 * Checks if a location is saved.
	 * 
	 * @param data
	 *            - the location data file
	 * @return True if a saved location exists, false otherwise.
	 */
	public static boolean isLocationSaved(final SharedPreferences data) {
		return getTime(data) != DEFAULT_TIME;
	}

	public static void putAddress(final SharedPreferences data,
			final String address) {
		data.edit().putString(KEY_ADDRESS, address).commit();
	}

	/**
	 * Updates the location data stored in the data file.
	 * 
	 * @param context
	 * @param location
	 *            the new location
	 * @return true if refreshed successfully
	 */
	public static boolean putLocation(final SharedPreferences data,
			final Location location) {
		if (location == null) {
			return false;
		}

		final double lat = location.getLatitude();
		final double lng = location.getLongitude();
		final long time = location.getTime();
		return putPosition(data, lat, lng) && putTime(data, time);
	}

	public static boolean putPosition(final SharedPreferences data,
			final double lat, final double lng) {
		return data.edit().remove(KEY_ADDRESS)
				.putLong(KEY_LAT, Double.doubleToLongBits(lat))
				.putLong(KEY_LNG, Double.doubleToLongBits(lng)).commit();
	}

	private static boolean putTime(final SharedPreferences data, final long time) {
		return data.edit().putLong(KEY_TIME, time).commit();
	}
}