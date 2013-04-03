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
public class GingerbreadLocationFinder implements ILocationFinder {

  /**
   * Intent action to update the user location.
   */
  private static final String ACTION_UPDATE_LOCATION = 
    GingerbreadLocationFinder.class.getPackage().getName() + ".ACTION_UPDATE_LOCATION";
 
  private final Context mContext;
  private final LocationManager mLocationManager;
  private final OnLocationUpdateListener mCallback;
  private final PendingIntent mUpdateIntent;
  
  private Location mCurrentLocation = null;
  
  /**
   * This {@link BroadcastReceiver} listens for a single location
   * update before unregistering itself.
   * The oneshot location update is returned via the {@link LocationListener}
   * specified in {@link setChangedLocationListener}.
   */
  protected BroadcastReceiver singleUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
    	// release resources
    	context.unregisterReceiver(singleUpdateReceiver);
        GingerbreadLocationFinder.this.cancel();
        // evaluate update
        if (mCallback != null){
    	  String key = LocationManager.KEY_LOCATION_CHANGED;
    	  Location newLocation = (Location) intent.getExtras().get(key);
    	  Location betterLocation =
    			  LocationHelper.betterLocation(newLocation, mCurrentLocation);
          mCallback.onLocationUpdate(betterLocation);
        }
    }
  };

  /**
   * Construct a new GingerbreadLocationFinder.
   * @param context Context
   */
  GingerbreadLocationFinder(Context context, OnLocationUpdateListener callback) {
	  mContext = context;
	  mCallback = callback;
	  // create the PendingIntent that will be broadcast by the oneshot update 
	  mUpdateIntent = PendingIntent.getBroadcast(context, 0,
	      		new Intent(ACTION_UPDATE_LOCATION),
	      		PendingIntent.FLAG_UPDATE_CURRENT);
	  mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void oneShotUpdate(Location currentBestLocation) {
	// the current location, for the receiver to compare it to the new one
	mCurrentLocation = currentBestLocation;
    // register the receiver
	IntentFilter filter = new IntentFilter(ACTION_UPDATE_LOCATION);
  	mContext.registerReceiver(singleUpdateReceiver, filter);
  	// define the criteria for the location update
	Criteria criteria = new Criteria();
	criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	// request the location update
  	mLocationManager.requestSingleUpdate(criteria, mUpdateIntent);
  }  
	    	
  /**
   * {@inheritDoc}
   */
  public void cancel() {
    mLocationManager.removeUpdates(mUpdateIntent);
  }
}
