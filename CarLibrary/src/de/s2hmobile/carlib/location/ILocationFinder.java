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

import android.location.Location;

/**
 * Interface definition for a Last Location Finder.
 * 
 * Classes that implement this interface must provide methods to find the "best"
 * (most accurate and timely) previously detected location using whatever
 * providers are available.
 * 
 * Where a timely / accurate previous location is not detected, classes should
 * return the last location and create a one-shot update to find the current
 * location. The one-shot update should be returned via the Location Listener
 * passed in through setChangedLocationListener.
 */
public interface ILocationFinder {

	/**
	 * create a one-shot update of the current location fix
	 * 
	 * @param listener
	 *            the listener that may receive a one-shot current location
	 *            update
	 * @param currentBestLocation
	 *            the current best fix
	 */
	public void oneShotUpdate(Location currentBestLocation);

	/**
	 * Cancel the one-shot current location update.
	 */
	public void cancel();
}
