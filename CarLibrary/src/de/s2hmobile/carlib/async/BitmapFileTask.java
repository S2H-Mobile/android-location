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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class BitmapFileTask extends BitmapBaseTask {

	private final OnBitmapRenderedListener mCallback;

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
	public BitmapFileTask(OnBitmapRenderedListener listener, String path,
			ImageView imageView) {
		super(imageView);
		mCallback = listener;
		mPath = path;
	}

	@Override
	protected Bitmap doInBackground(Integer... params) {
		final int width = params[0];
		final int height = params[1];
		return decodeBitmapFromFile(mPath, width, height);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (super.isCancelled()) {
			bitmap = null;
		}
		if (mViewReference != null && bitmap != null && mCallback != null) {
			final ImageView imageView = mViewReference.get();
			if (imageView != null) {
				mCallback.onBitmapRendered(imageView, bitmap);
			}
		}
	}

	private static Bitmap decodeBitmapFromFile(final String path,
			final int targetWidth, final int targetHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();

		/*
		 * Read the dimensions of the source image prior to construction (and
		 * memory allocation) of the target bitmap.
		 */
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// raw height and width of image
		final int imageWidth = options.outWidth;
		final int imageHeight = options.outHeight;

		// decode the image file into a bitmap
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateInSampleSize(imageHeight, imageWidth,
				targetHeight, targetWidth);
		options.inPurgeable = true;
		return BitmapFactory.decodeFile(path, options);
	}
}