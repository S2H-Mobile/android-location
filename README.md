# android-location
Legacy library for Android applications, based on [android-protips-location](https://code.google.com/archive/p/android-protips-location/).

This library makes it easier for you to access the device location without using the proprietary Google Play Services.

## Setup
- Import the project folder as a library into the Eclipse ADT workspace.
- Add the library to an existing Android application: 

1. Right-click on your application in Eclipse -> _Properties_
2. Select _Android_ -> _Library_
3. Click on _Add ..._
4. Select the library and click _Ok_

## Usage

```java
package de.s2hmobile.location;

import android.location.Location;
import android.os.Bundle;

import de.s2hmobile.location.LocationHelper;
import de.s2hmobile.location.LocationHelper.OnLocationUpdateListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Location location = LocationHelper.getLastBestLocation(this, 100);

        // TODO use last location here
        
        LocationHelper.requestLocation(this, new OnLocationUpdateListener(){

			@Override
			public void onLocationUpdate(Location location) {

				// TODO use updated location here

			}});
    }
}

```

