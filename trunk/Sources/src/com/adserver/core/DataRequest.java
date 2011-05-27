package com.adserver.core;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.HttpConnection;

import com.adserver.browser.UniversalConnectionFactory;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.transport.ConnectionDescriptor;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class DataRequest {
	
	/**
	 * 
	 * @param url
	 * @return response string
	 * @throws IOException
	 */
	public static String getResponse(String url) throws IOException{
		int redirects = 0;
		byte[] data = null;
		HttpConnection connection = null;
	    InputStream is = null;
	    try {
	    	while (true) {
		    	connection = null;
		    	connection = openHttpConnection(url);
		    	if (null == connection) return null;
	    		connection.setRequestProperty("User-Agent", AutoDetectParameters.getInstance().getUserAgent());
		    	int rc = connection.getResponseCode();
		    	is = connection.openInputStream();

				if (rc == HttpConnection.HTTP_TEMP_REDIRECT
						|| rc == HttpConnection.HTTP_SEE_OTHER
						|| rc == HttpConnection.HTTP_MOVED_PERM
						|| rc == HttpConnection.HTTP_MOVED_TEMP) {
					String loc = connection.getHeaderField("Location");
					url = genRedirURL(url, loc);
					if (url.length() > 4 && redirects++ < 10) {
						try {
							if (is != null)
								is.close();
						} catch (Exception e) {
							// Ignore
						}
						try {
							if (connection != null)
								connection.close();
						} catch (Exception e) {
							// Ignore
						}
						continue;
					}

				}
				if (rc != HttpConnection.HTTP_OK)
					throw new IOException("Responce code = " + rc + " for " + url);
				break;
		    }

			 // Get the length and process the data
            int lenght = (int)connection.getLength();
            if (lenght > 0) {
                int actual = 0;
                int bytesread = 0 ;
                data = new byte[lenght];
                while ((bytesread != lenght) && (actual != -1)) {
                   actual = is.read(data, bytesread, lenght - bytesread);
                   bytesread += actual;
                }
            } else {
            	data = IOUtilities.streamToBytes(is);
            }
	    } catch (Exception e) {
//			Logger.debug(e.getMessage());
			throw new IOException(e.toString());
		} finally {
            // Close InputStream
            if(is != null)
            {
                try
                {
                    is.close();
                }
                catch(IOException e)
                {
                }
            }

            // Close Connection
            try
	            {
	                connection.close();
	            }catch(IOException ioe)
	            {
	            }
		}
			return new String (data);
	}

	private static String genRedirURL(String url, String redir) {
		/* Location has full URL */
		int url_index = redir.indexOf("://");
		if (url_index >= 0 && url_index <= 10)
			return redir;

		String host;
		String path;
		int slash_index = url.indexOf("/", 9);

		if (slash_index >= 0) {
			host = url.substring(0, slash_index);
		} else
			host = url;

		if (redir.startsWith("/")) {
			path = redir;
		} else if (slash_index >= 0) {
			String base = url.substring(slash_index);
			int get_index = base.indexOf("?");
			if (get_index >= 0)
				base = base.substring(0, get_index);
			int path_index = stringLastIndexOf(base, "/");
			if (path_index >= 0)
				base = base.substring(0, path_index);
			path = base + "/" + redir;
		} else
			path = "/" + redir;

		return host + path;
	}

	private static int stringLastIndexOf(String haystack, String needle) {
		int index, lastIndex = -1;
		while ((index = haystack.indexOf(needle, lastIndex + 1)) >= 0)
			lastIndex = index;
		return lastIndex;
	}

	
    public static HttpConnection openHttpConnection(String url) throws IOException {
    	HttpConnection httpConnection = null;
    	UniversalConnectionFactory connectionFactory = new UniversalConnectionFactory();
    	ConnectionDescriptor connectionDescriptor = connectionFactory.getConnection(url);
    	if (null != connectionDescriptor) {
    		httpConnection = (HttpConnection) connectionDescriptor.getConnection();
    	}
    	return httpConnection;
    }
}
