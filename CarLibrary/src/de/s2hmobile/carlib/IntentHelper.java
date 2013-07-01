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

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.preference.PreferenceManager;

public final class IntentHelper {

	static enum RoutingOptions {
		GENERIC(0, "geo:%1$f,%2$f") {
			@Override
			Intent getIntent(double lat, double lng) {
				return buildIntent(getPattern(), lat, lng);
			}
		},
		GOOGLE(1, "google.navigation:q=%1$f,%2$f") {
			@Override
			Intent getIntent(double lat, double lng) {
				return buildIntent(getPattern(), lat, lng);
			}
		},
		NAVIGON(3, "android.intent.action.navigon.START_PUBLIC") {
			@Override
			Intent getIntent(double lat, double lng) {
				final Intent intent = new Intent(getPattern());
				intent.putExtra("latitude", (float) lat);
				intent.putExtra("longitude", (float) lng);
				return intent;
			}
		},
		WAZE(2, "waze://?ll=%1$f,%2$f") {
			@Override
			Intent getIntent(double lat, double lng) {
				return buildIntent(getPattern(), lat, lng);
			}
		};

		/** Field value must correspond to the value in the XML string array. */
		private final int mIndex;

		private final String mPattern;

		private RoutingOptions(int index, String pattern) {
			mIndex = index;
			mPattern = pattern;
		}

		int getIndex() {
			return mIndex;
		}

		abstract Intent getIntent(double lat, double lng);

		String getPattern() {
			return mPattern;
		}

		private static final Intent buildIntent(String pattern, double lat,
				double lng) {
			final String data = String.format(Locale.US, pattern, lat, lng);
			return new Intent(Intent.ACTION_VIEW, Uri.parse(data));
		}
	}

	private IntentHelper() {
	}

	/**
	 * Adds the flags {@link Intent#FLAG_ACTIVITY_CLEAR_TOP} and
	 * {@link Intent#FLAG_ACTIVITY_NEW_TASK} to the intent.
	 * 
	 * @param intent
	 *            - the intent to add the flags to
	 */
	public static final void addFlags(final Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static final Intent getNavigationIntent(final Context context,
			final double lat, final double lng) {

		// get the currently selected value from prefs
		final int index = getSelectedRoutingOption(
				PreferenceManager.getDefaultSharedPreferences(context),
				context.getResources());

		// select the appropriate intent
		Intent intent = null;
		for (RoutingOptions selectedOption : RoutingOptions.values()) {
			if (index == selectedOption.getIndex()) {
				intent = selectedOption.getIntent(lat, lng);
			}
		}

		if (IntentHelper.isIntentSafe(context, intent,
				PackageManager.MATCH_DEFAULT_ONLY)) {

			// the app is installed, so return the intent
			IntentHelper.addFlags(intent);
			return intent;
		} else {

			// app is not installed, return intent to Google Play?
			return null;
		}
	}

	/**
	 * Uses the {@link PackageManager} to check if the intent can be handled.
	 * 
	 * @param context
	 *            - for the package manager
	 * @param intent
	 *            - the intent to evaluate
	 * @param flag
	 *            - for querying, indicates the package category
	 * @return true if there is at least one activity that can handle the intent
	 */
	public static final boolean isIntentSafe(final Context context,
			final Intent intent, final int flag) {
		final PackageManager pm = context.getPackageManager();
		final List<ResolveInfo> activities = pm.queryIntentActivities(intent,
				flag);
		return activities.size() > 0;
	}

	/**
	 * Launches another app, when we know the package name only. If the app is
	 * not installed, redirects to the Google Play detail page for this app.
	 * 
	 * @param context
	 *            - for the package manager
	 * @param packageName
	 *            - the package name of the app to launch
	 */
	public static final void launchApp(Context context, String packageName) {
		final PackageManager pm = context.getPackageManager();

		// check if app is installed
		if (IntentHelper.isInstalled(pm, packageName)) {

			// get the main activity and launch it
			final Intent intent = pm.getLaunchIntentForPackage(packageName);
			addFlags(intent);
			context.startActivity(intent);
		} else {

			// launch the Google Play detail page
			IntentHelper.launchGooglePlay(context, packageName);
		}
	}

	/**
	 * Launch the Google Play detail page for the given app.
	 * 
	 * @param context
	 *            - for the package manager
	 * @param packageName
	 *            - the package name of the app
	 */
	public static final void launchGooglePlay(Context context,
			String packageName) {

		// start building the Uri by appending the package name as parameter
		final Uri.Builder builder = new Uri.Builder().appendQueryParameter(
				"id", packageName);

		// build the intent to the Google Play Store
		Intent intent = openGooglePlayApp(builder);

		// check if Google Play Store is installed
		if (!isIntentSafe(context, intent, PackageManager.MATCH_DEFAULT_ONLY)) {

			// reassign the intent to launch the Google Play website
			intent = openGooglePlayBrowser(builder);
		}

		addFlags(intent);
		context.startActivity(intent);
	}

	public static final CharSequence updateRoutingSummary(
			final SharedPreferences prefs, final Resources res) {

		// get the currently selected value from prefs
		final int index = getSelectedRoutingOption(prefs, res);

		// return the corresponding entry of the string array
		final String[] values = res.getStringArray(R.array.routing_entries);
		return values[index];
	}

	/**
	 * Determine the currently selected user option for routing from the shared
	 * preferences file.
	 * 
	 * @param prefs
	 * @param res
	 * @return The index of the currently selected routing option, as defined in
	 *         the XML string array.
	 * @throws NotFoundException
	 * @throws NumberFormatException
	 */
	private static final int getSelectedRoutingOption(
			final SharedPreferences prefs, final Resources res)
			throws NotFoundException, NumberFormatException {
		final String key = res.getString(R.string.key_routing);
		final String defaultValue = res.getString(R.string.routing_default);
		final String entry = prefs.getString(key, defaultValue);
		return Integer.valueOf(entry);
	}

	/**
	 * Checks if a package is installed.
	 * 
	 * @param pm
	 *            - the package manager
	 * @param packageName
	 *            - the name of the package
	 * @return true if package is installed
	 */
	private static final boolean isInstalled(PackageManager pm,
			String packageName) {
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * Build the intent to launch Google Play Store entry for the app defined in
	 * the {@link Uri.Builder} object.
	 * 
	 * @param builder
	 *            - the {@link Uri.Builder} object containing the package name
	 *            as Uri parameter
	 * @return The intent including the Uri data.
	 */
	private static final Intent openGooglePlayApp(Uri.Builder builder) {
		final Uri uri = builder.scheme("market").authority("details").build();
		return new Intent(Intent.ACTION_VIEW, uri);
	}

	/**
	 * Build the intent to open the detail page on the Google Play website for
	 * the app defined in the {@link Uri.Builder} object.
	 * 
	 * @param builder
	 *            - the {@link Uri.Builder} object containing the package name
	 *            as Uri parameter
	 * @return The intent including the Uri data.
	 */
	private static final Intent openGooglePlayBrowser(Uri.Builder builder) {
		final Uri uri = builder.scheme("http").authority("play.google.com")
				.appendEncodedPath("store/apps/details").build();
		return new Intent(Intent.ACTION_VIEW, uri);
	}
}