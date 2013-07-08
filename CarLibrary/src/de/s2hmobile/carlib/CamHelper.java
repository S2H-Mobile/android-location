/*
 * Copyright (C) 2012 S2H Mobile
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

package de.s2hmobile.carlib;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class CamHelper {

	public static final int REQ_TAKE_PICTURE = 0x100;

	private CamHelper() {
	}

	/**
	 * Checks if the user has opted to take pictures.
	 * 
	 * @param context
	 *            the application context
	 * @return true if taking pictures is enabled
	 */
	public static boolean isCameraEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		final String key = context.getResources()
				.getString(R.string.key_camera);
		boolean defValue = context.getResources().getBoolean(
				R.bool.pref_camera_default);
		return prefs.getBoolean(key, defValue);
	}

	public static Intent takePicture(Context context, File file) {
		if (hasCamera(context)) {
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (IntentHelper.isIntentSafe(context, intent)) {
				final Uri uri = Uri.fromFile(file);
				if (uri != null) {
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				}
				return intent;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private static boolean hasCamera(Context context) {
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA);
	}
}
