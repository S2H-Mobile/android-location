/*
 * Copyright (C) 2012 - 2014, S2H Mobile
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
 * Provides static access to a preferences file containing the location data.
 * 
 * @author Stephan Hoehne
 */
public final class LocationData {

	/**
	 * Key for saving the address associated with the location in the data file.
	 */
	private static final String KEY_ADDRESS = "pref_address";

	/** Key for saving the latitude in the data file. */
	private static final String KEY_LAT = "pref_lat";

	/** Key for saving the longitude in the data file. */
	private static final String KEY_LNG = "pref_lng";

	/** Key for saving the timestamp in the data file. */
	private static final String KEY_TIME = "pref_time";

	private LocationData() {
	}

	/**
	 * Returns the address of the location, if one has been saved. There can be
	 * a saved location without an address.
	 * 
	 * @param data
	 *            - the location data file
	 * @return The stored address string, or null.
	 */
	public static String getAddress(final SharedPreferences data) {
		return data.getString(LocationData.KEY_ADDRESS, null);
	}

	/**
	 * Reads the coordinates of the stored location from the preferences file.
	 * 
	 * @param data
	 *            - the file
	 * @return The coordinate array or (0,0) if nothing has been saved.
	 */
	public static Double[] getPosition(final SharedPreferences data) {

		// read coordinates from data file
		final long lat = data.getLong(KEY_LAT, Long.MIN_VALUE);
		final long lng = data.getLong(KEY_LNG, Long.MIN_VALUE);

		if (lat == Long.MIN_VALUE || lng == Long.MIN_VALUE) {
			return null;
		}

		// convert to double and initialize the array
		final double latitude = Double.longBitsToDouble(lat);
		final double longitude = Double.longBitsToDouble(lng);

		final Double[] coordinates = { latitude, longitude };
		return coordinates;
	}

	public static long getTime(final SharedPreferences data) {
		return data.getLong(KEY_TIME, Long.MIN_VALUE);
	}

	public static void putAddress(final SharedPreferences data,
			final String address) {
		data.edit().putString(KEY_ADDRESS, address).commit();
	}

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