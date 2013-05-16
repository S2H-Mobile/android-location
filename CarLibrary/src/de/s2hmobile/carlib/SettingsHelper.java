package de.s2hmobile.carlib;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;

public class SettingsHelper {

	private SettingsHelper() {
	}

	// keys for saving settings in default file
	public static final String KEY_ROUTING = "pref_routing";
	private static final String KEY_CAMERA = "pref_camera";

	private static final int VAL_CREATE_CHOOSER = 0, VAL_GOOGLE_NAVIGATION = 1,
			VAL_NAVIGON = 2;

	// patterns for building the navigation uri
	private static final String PATTERN_GOOGLE_NAVIGATION = "google.navigation:q=%1$f,%2$f";

	private static final String PATTERN_GENERIC_APP = "geo:%1$f,%2$f";

	/**
	 * Keys for Navigon intent data
	 */
	private static final String INTENT_ACTION_START_MN = "android.intent.action.navigon.START_PUBLIC",
			INTENT_EXTRA_KEY_LATITUDE = "latitude",
			INTENT_EXTRA_KEY_LONGITUDE = "longitude";

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
		boolean defValue = context.getResources().getBoolean(
				R.bool.pref_camera_default);
		return prefs.getBoolean(KEY_CAMERA, defValue);
	}

	public static CharSequence newRoutingSummary(SharedPreferences prefs,
			Resources res) {
		String[] entries = res.getStringArray(R.array.routing_entries);
		String request = prefs.getString(KEY_ROUTING,
				res.getString(R.string.routing_default));
		Integer requestId = Integer.valueOf(request);
		return entries[requestId];
	}

	public static Intent getNavigationIntent(Context context, double lat,
			double lng) {
		String defValue = context.getResources().getString(
				R.string.routing_default);
		String request = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(KEY_ROUTING, defValue);
		int requestId = Integer.valueOf(request);
		Intent result = null;
		switch (requestId) {

		case VAL_CREATE_CHOOSER:
			Intent intent = buildIntentData(PATTERN_GENERIC_APP, lat, lng);
			addFlags(intent);
			String message = context.getResources().getString(
					R.string.dialog_choose_carlib);
			result = Intent.createChooser(intent, message);
			break;

		case VAL_GOOGLE_NAVIGATION:
			result = buildIntentData(PATTERN_GOOGLE_NAVIGATION, lat, lng);
			addFlags(result);
			break;

		case VAL_NAVIGON:
			result = new Intent(INTENT_ACTION_START_MN);
			result.putExtra(INTENT_EXTRA_KEY_LATITUDE, (float) lat);
			result.putExtra(INTENT_EXTRA_KEY_LONGITUDE, (float) lng);
			addFlags(result);
			break;

		default:
			// returns null to the calling activity
			break;

		}
		if (!isIntentSafe(context, result, PackageManager.MATCH_DEFAULT_ONLY)) {
			result = null;
		}
		return result;
	}

	/**
	 * Uses {@link Intent#addFlags()} to add the flags
	 * <code>Intent.FLAG_ACTIVITY_CLEAR_TOP</code> in conjunction with
	 * <code>Intent.FLAG_ACTIVITY_NEW_TASK</code> to the intent.
	 */
	public static void addFlags(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	/**
	 * Encodes latitude and longitude in a data uri and creates an intent to
	 * view the geolocation.
	 * 
	 * @param pattern
	 *            the template for the data string
	 * @param lat
	 *            the latitude
	 * @param lng
	 *            the longitude
	 * @return the intent including the data uri
	 */
	private static Intent buildIntentData(String pattern, double lat, double lng) {
		String data = String.format(Locale.US, pattern, lat, lng);
		return new Intent(Intent.ACTION_VIEW, Uri.parse(data));
	}

	/**
	 * Uses the {@link PackageManager} to check if the intent can be handled.
	 * 
	 * @param context
	 *            for the package manager
	 * @param intent
	 *            the intent to evaluate
	 * @param flag
	 *            for querying, indicates the package category
	 * @return true if there is at least one activity that can handle the intent
	 */
	private static boolean isIntentSafe(Context context, Intent intent, int flag) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(intent, flag);
		return activities.size() > 0;
	}
}
