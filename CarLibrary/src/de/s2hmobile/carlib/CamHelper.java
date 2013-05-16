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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

public class CamHelper {

	public static final int REQ_TAKE_PICTURE = 1;

	private CamHelper() {
	}

	public static Intent takePicture(Context context, String fileName) {
		Intent intent = null;
		if (hasCamera(context)) {
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			int flag = PackageManager.MATCH_DEFAULT_ONLY;
			if (IntentHelper.isIntentSafe(context, intent, flag)) {
				android.net.Uri uri = ImageFileHandler.getFileUri(fileName);
				if (uri != null) {
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				}
			} else {
				intent = null;
			}
		}
		return intent;
	}

	private static boolean hasCamera(Context context) {
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA);
	}
}
