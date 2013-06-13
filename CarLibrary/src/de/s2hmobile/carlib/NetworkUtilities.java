package de.s2hmobile.carlib;

import java.net.URI;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Provides utility methods for communicating with a backend server via Http.
 * 
 * @author Stephan Hoehne
 * 
 */
public final class NetworkUtilities {

	/**
	 * Enumeration of Http methods.
	 * 
	 * @author Stephan Hoehne
	 */
	public static enum HttpMethod {
		DELETE {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpDelete(uri);
			}
		},
		GET {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpGet(uri);
			}
		},
		POST {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpPost(uri);
			}
		},
		PUT {
			public final HttpRequestBase getRequest(URI uri) {
				return new HttpPut(uri);
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

}