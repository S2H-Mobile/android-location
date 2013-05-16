package de.s2hmobile.carlib;

import java.net.URI;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Build;

/**
 * Provides utility methods for communicating with a backend server via Http.
 * 
 * @author Stephan Hoehne
 * 
 */
public final class NetworkUtilities {

	/** User agent string, to be displayed in the server logs. **/
	private static final String USER_AGENT = "Android " + Build.VERSION.RELEASE;

	public static final boolean IS_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

	/** Timeout in milliseconds for each Http request. **/
	public static final int HTTP_REQUEST_TIMEOUT_MS = 120 * 1000; // 2 mins

	/**
	 * Enumeration of Http methods.
	 * 
	 * @author Stephan Hoehne
	 */
	public static enum HttpMethod {
		POST {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpPost(uri);
			}
		},
		GET {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpGet(uri);
			}
		},
		PUT {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpPut(uri);
			}
		},
		DELETE {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpDelete(uri);
			}
		};

		/**
		 * Get an instance of the Http method.
		 * 
		 * @see <a
		 *      href="http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/methods/package-summary.html">Documentation</a>
		 * 
		 * @return the http method
		 */
		public abstract HttpRequestBase getRequest(URI uri);
	}

	private NetworkUtilities() {
	}

	/**
	 * Sets up an AndroidHttpClient.
	 * 
	 * @return the Http client
	 */
	public static AndroidHttpClient getAndroidHttpClient() {
		AndroidHttpClient client = AndroidHttpClient.newInstance(USER_AGENT);
		// set the timeout parameters
		final HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params,
				HTTP_REQUEST_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
		ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
		return client;
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}
}