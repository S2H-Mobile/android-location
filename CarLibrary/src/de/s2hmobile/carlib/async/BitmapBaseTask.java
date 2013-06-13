package de.s2hmobile.carlib.async;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.widget.ImageView;

abstract class BitmapBaseTask extends AsyncTask<Integer, Void, Bitmap> {

	protected final WeakReference<ImageView> mViewReference;

	protected BitmapBaseTask(ImageView imageView) {
		mViewReference = new WeakReference<ImageView>(imageView);
	}

	/**
	 * Determines the factor to scale down the source image. Compares the
	 * dimensions of source and target image and calculates the smallest ratio.
	 * Determines the scale factor by calculating the power of two that is
	 * closest to this ratio.
	 * 
	 * @param imageWidth
	 *            width of original image
	 * @param imageHeight
	 *            height of original image
	 * @param reqWidth
	 *            requested width of target image
	 * @param reqHeight
	 *            requested height of target image
	 * @return the scale factor
	 */
	protected static int calculateInSampleSize(final int imageHeight,
			final int imageWidth, final int reqHeight, final int reqWidth) {

		// init the size ratio between source and target
		int ratio = 1;

		/*
		 * Check if the requested size of the target bitmap is positive to avoid
		 * dividing by zero. Check if the original image is actually larger than
		 * the target image.
		 */
		if (reqWidth != 0 && reqHeight != 0
				&& (imageHeight > reqHeight || imageWidth > reqWidth)) {

			// calculate height and width ratios
			final int heightRatio = Math.round((float) imageHeight
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) imageWidth
					/ (float) reqWidth);

			/*
			 * Don't scale down too much, so choose the smallest ratio. This
			 * will guarantee a final image with both dimensions larger than or
			 * equal to the requested ones.
			 */
			ratio = Math.min(heightRatio, widthRatio);
		}

		/*
		 * Determine the power of two that is closest to and smaller than the
		 * scale factor.
		 */
		int temp = 2;
		while (temp <= ratio) {
			temp *= 2;
		}
		final int scaleFactor = temp / 2;
		return scaleFactor;
	}
}