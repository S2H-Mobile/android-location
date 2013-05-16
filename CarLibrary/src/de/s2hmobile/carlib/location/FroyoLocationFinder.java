/*  File modified by S2H Mobile, 2012.
 * 
 * Copyright 2011 Google Inc.
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

package de.s2hmobile.carlib.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import de.s2hmobile.carlib.location.LocationHelper.OnLocationUpdateListener;

public class FroyoLocationFinder implements ILocationFinder {
	private final Context mContext;
	private final LocationManager mLocationManager;
	private final OnLocationUpdateListener mCallback;

	private Location mCurrentLocation = null;

	/**
	 * This one-off {@link LocationListener} simply listens for a single
	 * location update before unregistering itself. The one-off location update
	 * is returned via the {@link LocationListener} specified in
	 * {@link setChangedLocationListener}.
	 */
	protected LocationListener singleUpdateListener = new LocationListener() {

		public void onLocationChanged(Location newLocation) {
			// release resources by removing updates
			FroyoLocationFinder.this.cancel();
			// evaluate update
			if (mCallback != null) {
				Location betterLocation = LocationHelper.betterLocation(
						newLocation, mCurrentLocation);
				mCallback.onLocationUpdate(betterLocation);
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	/**
	 * Construct a new FroyoLocationFinder.
	 * 
	 * @param context
	 *            for the system service and to get the main looper
	 */
	FroyoLocationFinder(Context context, OnLocationUpdateListener callback) {
		mContext = context;
		mCallback = callback;
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void oneShotUpdate(Location currentBestLocation) {
		// the current location, for the receiver to compare it to the new one
		mCurrentLocation = currentBestLocation;
		// define the criteria and the best provider for the location update
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = mLocationManager.getBestProvider(criteria, true);
		if (provider != null) {
			// request the location update
			mLocationManager.requestLocationUpdates(provider, 0, 0,
					singleUpdateListener, mContext.getMainLooper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void cancel() {
		mLocationManager.removeUpdates(singleUpdateListener);
	}
}