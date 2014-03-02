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

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Singleton that provides static access to a preferences file containing the
 * location data.
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

	/** Key for accuracy in location data file. */
	private static final String KEY_ACC = "data_accuracy";

	/** The file name to be appended to the package name. */
	private static final String FILE_NAME = ".LOCATION_DATA";

	private final SharedPreferences mFile;

	private static LocationData instance = null;

	private LocationData(final Context context) {
		final Context appContext = context.getApplicationContext();
		final String name = appContext.getApplicationInfo().packageName
				+ FILE_NAME;
		mFile = appContext.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public boolean clear() {
		return mFile.edit().clear().commit();
	}

	public float getAccuracy() {
		return mFile.getFloat(KEY_ACC, Float.MAX_VALUE);
	}

	/**
	 * Returns the address of the location, if one has been saved. There can be
	 * a saved location without an address.
	 * 
	 * @param data
	 *            - the location data file
	 * @return The stored address string, or null.
	 */
	public String getAddress() {
		return mFile.getString(LocationData.KEY_ADDRESS, null);
	}

	/**
	 * Reads the coordinates of the stored location from the preferences file.
	 * 
	 * @param data
	 *            - the file
	 * @return The coordinate array or (0,0) if nothing has been saved.
	 */
	public Double[] getPosition() {

		// coordinates from data file
		final long lat = mFile.getLong(KEY_LAT, Long.MIN_VALUE);
		final long lng = mFile.getLong(KEY_LNG, Long.MIN_VALUE);

		if (lat == Long.MIN_VALUE || lng == Long.MIN_VALUE) {
			return null;
		}

		// convert to double and initialize the array
		final double latitude = Double.longBitsToDouble(lat);
		final double longitude = Double.longBitsToDouble(lng);

		final Double[] coordinates = { latitude, longitude };
		return coordinates;
	}

	public long getTime() {
		return mFile.getLong(KEY_TIME, Long.MIN_VALUE);
	}

	public boolean putAddress(final String address) {
		return mFile.edit().putString(KEY_ADDRESS, address).commit();
	}

	public boolean putLocation(final Location location) {
		if (location == null) {
			return false;
		}

		final double lat = location.getLatitude();
		final double lng = location.getLongitude();
		final long time = location.getTime();
		final float accuracy = location.getAccuracy();
		return putPosition(lat, lng)
				&& mFile.edit().putFloat(KEY_ACC, accuracy)
						.putLong(KEY_TIME, time).commit();
	}

	public boolean putPosition(final double lat, final double lng) {
		return mFile.edit().remove(KEY_ADDRESS)
				.putLong(KEY_LAT, Double.doubleToLongBits(lat))
				.putLong(KEY_LNG, Double.doubleToLongBits(lng)).commit();
	}

	public static LocationData get(final Context context) {
		if (instance == null) {
			instance = new LocationData(context);
		}
		return instance;
	}
}