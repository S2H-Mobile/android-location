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

public final class LocationData {

	private LocationData() {
	}

	private static final long DEFAULT_TIME = Long.MIN_VALUE;

	/**
	 * Keys for location data file.
	 */
	private static final String KEY_ADDRESS = "pref_address";
	private static final String 
			KEY_TIME = "pref_time", KEY_LAT = "pref_lat", KEY_LNG = "pref_lng";

	/**
	 * Checks if a location is saved.
	 * 
	 * @param data
	 *            the location data file
	 * @return true if a saved location exists, false otherwise
	 */
	public static boolean isLocationSaved(SharedPreferences data) {
		return getTime(data) != DEFAULT_TIME;
	}

	/**
	 * Reads the location data from the preferences file.
	 * 
	 * @param data
	 *            - the location data file
	 * @return The coordinate array or (0,0) if nothing has been
	 *         saved.
	 */
	public static Double[] getPosition(SharedPreferences data) {
		
		// read coordinates from data file
		final long lat = data.getLong(KEY_LAT, 0L);
		final long lng = data.getLong(KEY_LNG, 0L);
		
		// convert to double and initialize the array
		double latitude = Double.longBitsToDouble(lat), longitude = Double
				.longBitsToDouble(lng);
		final Double[] coordinates = { latitude, longitude };
		return coordinates;
	}

	public static boolean putPosition(SharedPreferences data, double lat,
			double lng) {
		return data.edit().remove(KEY_ADDRESS)
				.putLong(KEY_LAT, Double.doubleToLongBits(lat))
				.putLong(KEY_LNG, Double.doubleToLongBits(lng)).commit();
	}

	public static long getTime(SharedPreferences data) {
		return data.getLong(KEY_TIME, DEFAULT_TIME);
	}

	private static boolean putTime(SharedPreferences data, long time) {
		return data.edit().putLong(KEY_TIME, time).commit();
	}

	/**
	 * Returns the address of the parking spot, if one has been saved. There can
	 * be a saved location without an address.
	 * 
	 * @param data
	 *            the location data file
	 * @return the saved address, or an empty string
	 */
	public static String getAddress(SharedPreferences data) {
		return data.getString(LocationData.KEY_ADDRESS, "");
	}

	public static void putAddress(SharedPreferences data, String address) {
		data.edit().putString(KEY_ADDRESS, address).commit();
	}

	public static void clearLocationDataFile(SharedPreferences data) {
		data.edit().clear().commit();
	}

	/**
	 * Updates the location data stored in the data file.
	 * 
	 * @param context
	 * @param location
	 *            the new location
	 * @return true if refreshed successfully
	 */
	public static boolean putLocation(SharedPreferences data, Location location) {
		if (location != null) {
			double lat = location.getLatitude(), lng = location.getLongitude();
			long time = location.getTime();
			return putPosition(data, lat, lng) && putTime(data, time);
		} else {
			return false;
		}
	}
}