/*  File modified by S2H Mobile, 2013.
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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import de.s2hmobile.carlib.location.LocationHelper.OnLocationUpdateListener;

/**
 * Optimized implementation of ILocationFinder for devices running Gingerbread
 * and above.
 */
public class GingerbreadLocationFinder extends LocationFinderBase {

	/** Intent action to update the user location. */
	private static final String ACTION_UPDATE_LOCATION = GingerbreadLocationFinder.class
			.getPackage().getName() + ".ACTION_UPDATE_LOCATION";

	private final PendingIntent mUpdateIntent;

	/**
	 * Listens for a single location update before unregistering itself. The
	 * one-shot location update is returned via the {@link LocationListener}
	 * specified in {@link setChangedLocationListener}.
	 */
	private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			// release resources
			cancel();

			// evaluate update
			if (mCallback != null) {
				final String key = LocationManager.KEY_LOCATION_CHANGED;
				final Location newLocation = (Location) intent.getExtras().get(
						key);

				// send back the better location
				final Location betterLocation = LocationFinderBase
						.betterLocation(newLocation, mCurrentLocation);
				mCallback.onLocationUpdate(betterLocation);
			}
		}

	};

	GingerbreadLocationFinder(final Context context,
			final OnLocationUpdateListener callback) {
		super(context, callback);

		// create the intent that will be broadcast by the one-shot update
		mUpdateIntent = PendingIntent.getBroadcast(context, 0, new Intent(
				ACTION_UPDATE_LOCATION), PendingIntent.FLAG_UPDATE_CURRENT);
	}

	/** {@inheritDoc} */
	@Override
	public void cancel() {
		mLocationManager.removeUpdates(mUpdateIntent);
		mContext.unregisterReceiver(mUpdateReceiver);
	}

	@Override
	protected void invokeBroadcast(final Criteria criteria) {

		// register the receiver
		final IntentFilter filter = new IntentFilter(ACTION_UPDATE_LOCATION);
		mContext.registerReceiver(mUpdateReceiver, filter);

		try {

			// request the location update
			mLocationManager.requestSingleUpdate(criteria, mUpdateIntent);
		} catch (final IllegalArgumentException e) {

			/*
			 * Most probably, no provider was found for criteria, because user
			 * has turned off all location sensors on the device. Release
			 * resources by cancelling updates.
			 */
			cancel();
		}
	}
}