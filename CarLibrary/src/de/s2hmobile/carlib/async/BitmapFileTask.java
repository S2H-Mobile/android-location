/*
 * Copyright (C) 2012 - 2013, S2H Mobile
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

package de.s2hmobile.carlib.async;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class BitmapFileTask extends AsyncTask<Integer, Void, Bitmap> {
	private OnBitmapRenderedListener mCallback = null;

	private final WeakReference<ImageView> imageViewReference;
	private final String mPath;

	/**
	 * Construct a BitmapFileTask.
	 * 
	 * @param activity
	 *            the activity executing this task
	 * @param path
	 *            the path to the image file
	 * @param imageView
	 *            the host view
	 */
	public BitmapFileTask(Object caller, String path, ImageView imageView) {
		try {
			mCallback = (OnBitmapRenderedListener) caller;
		} catch (ClassCastException e) {
			android.util.Log.e("BitmapFileTask",
					"Caller must implement OnBitmapRenderedListener.", e);
		}
		mPath = path;
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(Integer... params) {
		final int width = params[0], height = params[1];
		return decodeBitmapFromFile(mPath, width, height);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (super.isCancelled()) {
			bitmap = null;
		}
		if (imageViewReference != null && bitmap != null && mCallback != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				mCallback.onBitmapRendered(imageView, bitmap);
			}
		}
	}

	private static Bitmap decodeBitmapFromFile(String path, int targetWidth,
			int targetHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// read the dimensions and type of the image data prior to construction
		// (and memory allocation) of the bitmap
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int imageWidth = options.outWidth, imageHeight = options.outHeight;
		// determine the scaling factor, scale down only
		int scaleFactor = 2, realFactor = 1;
		if (targetWidth != 0 && targetHeight != 0
				&& (imageHeight > targetHeight || imageWidth > targetWidth)) {
			realFactor = Math.min(imageWidth / targetWidth, imageHeight
					/ targetHeight);
		}
		// TODO this is probably not necessary, the scale factor will be a power
		// of two anyway
		while (scaleFactor <= realFactor) {
			scaleFactor *= 2;
		}
		// decode the image file into a bitmap
		options.inJustDecodeBounds = false;
		options.inSampleSize = scaleFactor / 2;
		options.inPurgeable = true;
		return BitmapFactory.decodeFile(path, options);
	}
}
