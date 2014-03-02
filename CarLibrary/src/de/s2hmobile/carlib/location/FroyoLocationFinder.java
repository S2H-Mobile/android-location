/*  File modified by S2H Mobile, 2012 - 2013.
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
import android.os.Bundle;
import de.s2hmobile.carlib.location.LocationHelper.OnLocationUpdateListener;

public class FroyoLocationFinder extends LocationFinderBase {

	/**
	 * This one-off {@link LocationListener} simply listens for a single
	 * location update before unregistering itself. The one-off location update
	 * is returned via the {@link LocationListener}.
	 */
	private final LocationListener mListener = new LocationListener() {

		@Override
		public void onLocationChanged(final Location newLocation) {

			// release resources
			cancel();

			// evaluate update
			if (mCallback != null) {
				final Location betterLocation = LocationFinderBase
						.betterLocation(newLocation, mCurrentLocation);
				mCallback.onLocationUpdate(betterLocation);
			}
		}

		@Override
		public void onProviderDisabled(final String provider) {
		}

		@Override
		public void onProviderEnabled(final String provider) {
		}

		@Override
		public void onStatusChanged(final String provider, final int status,
				final Bundle extras) {
		}
	};

	/**
	 * Construct a new FroyoLocationFinder.
	 * 
	 * @param context
	 *            - for the system service and to get the main looper
	 */
	FroyoLocationFinder(final Context context,
			final OnLocationUpdateListener callback) {
		super(context, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void cancel() {
		mLocationManager.removeUpdates(mListener);
	}

	@Override
	protected void invokeBroadcast(final Criteria criteria) {
		final String provider = mLocationManager
				.getBestProvider(criteria, true);
		if (provider == null) {
			return;
		}

		// request the location update
		mLocationManager.requestLocationUpdates(provider, 0, 0, mListener,
				mContext.getMainLooper());
	}
}