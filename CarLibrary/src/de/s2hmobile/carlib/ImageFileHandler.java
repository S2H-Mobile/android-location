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

package de.s2hmobile.carlib;

import java.io.File;
import java.io.IOException;

import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import de.s2hmobile.carlib.async.AsyncTask;
import de.s2hmobile.carlib.async.BitmapFileTask;

public class ImageFileHandler {

	// cache the file handler
	private static File sFile = null;

	private ImageFileHandler() {
	}

	/**
	 * Executes a BitmapFileTask to render a bitmap from the given file.
	 * 
	 * @param activity
	 *            the calling activity
	 * @param fileName
	 *            the name of the image file
	 * @param view
	 *            the view displaying the bitmap
	 * @param width
	 *            the width of the target bitmap
	 * @param height
	 *            the height of the target bitmap
	 */
	public static void loadBitmap(Object caller, String fileName,
			ImageView view, int width, int height) {
		// public static void loadBitmap<T extends OnBitmapRenderedListener> ()
		File file = null;
		try {
			file = ImageFileHandler.getFile(fileName);
		} catch (IOException e) {
			handleException(e);
		}
		if (file != null && file.exists()) {
			final BitmapFileTask task = new BitmapFileTask(caller,
					file.getAbsolutePath(), view);
			final Integer[] params = { width, height };
			task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, params);
		}
	}

	public static Uri getFileUri(String name) {
		Uri result = null;
		try {
			result = Uri.fromFile(ImageFileHandler.getFile(name));
		} catch (IOException e) {
			handleException(e);
		}
		return result;
	}

	public static boolean deleteImageFile(String name) {
		boolean isDeleted = false;
		try {
			isDeleted = ImageFileHandler.getFile(name).delete();
		} catch (IOException e) {
			handleException(e);
		}
		return isDeleted;
	}

	private static File getFile(String name) throws IOException {
		if (sFile == null) {
			if (isExternalStorageWritable()) {
				File path = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				path.mkdirs();
				sFile = new File(path, name);
			} else {
				throw new IOException(
						"Can't create path to external storage directory.");
			}
		}
		return sFile;
	}

	private static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	private static void handleException(IOException e) {
		android.util.Log.e("ImageFileHandler",
				"IOException occured while handling file.", e);
	}
}