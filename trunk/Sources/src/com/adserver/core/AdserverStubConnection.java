package com.adserver.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.EncodedImage;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class AdserverStubConnection implements HttpConnection {
	private String				host;									// URL host
	private String				path;									// URL path
	private HttpHeaders			headers;								// Request headers
	private SocketConnection	socket			= null;
	private AdserverInputStream	is				= null;

	private String				responseCode	= "";
	private String				responseMessage	= "";
	private HttpHeaders			responseHeaders	= new HttpHeaders();

	/**
	 * Constructor
	 * 
	 * @param url
	 *            Request URL
	 * @param headers
	 *            Request headers
	 * @param application
	 * @throws IOException
	 */
	public AdserverStubConnection(final String url, final HttpHeaders headers, final AdserverBase application) throws IOException {
		boolean fetchingURL = true;
		StringBuffer headersLine = new StringBuffer(512);

		if (!Adserver.DEFAULT_HTML.equalsIgnoreCase(url) && !Adserver.DEFAULT_IMG.equalsIgnoreCase(url)
				&& !Adserver.DEFAULT_IMG_OS5.equalsIgnoreCase(url)) {
			parseURL(url);
		} else {
			host = Adserver.DEFAULT_HOST;
			path = "";
		}

		boolean isDefaultImage = false;

		InputStream is = null;
		boolean cacheMode = application.isCacheEnabled();
		while (fetchingURL) {
			responseHeaders.reset();
			headersLine = new StringBuffer(4096);
			if (Adserver.DEFAULT_HTML.equalsIgnoreCase(url)) {
				is = getClass().getResourceAsStream('/' + url);
			} else if (Adserver.DEFAULT_IMG.equalsIgnoreCase(url) || Adserver.DEFAULT_IMG_OS5.equalsIgnoreCase(url)) {
				EncodedImage image = EncodedImage.getEncodedImageResource(application.getDefaultImage());
				responseCode = "200";
				responseMessage = "OK";
				responseHeaders.addProperty("Content-Type", image.getMIMEType());
				responseHeaders.addProperty("Content-Length", String.valueOf(image.getData().length));
				is = getClass().getResourceAsStream('/' + application.getDefaultImage());
				isDefaultImage = true;
			} /*else {  // TODO TEST Disabled cache mode
				  String cacheFilename = application.getCacheFilename(url);
				  if (null != cacheFilename) {
				      is = buildCacheMode(cacheFilename, application);
				      cacheMode = true;
				  } else {
				      throw new IOException();
				  }
				}*/

			if (!isDefaultImage) {
				// Parsing response headers
				int pos;
				String line;
				while ((line = parseHeaderLine(is)).length() > 0) {
					line = line.trim();
					headersLine.append(line).append("\r\n");
					if (line.indexOf("HTTP/1.") == 0) {
						int blank1 = line.indexOf(' ');
						int blank2 = line.indexOf(' ', blank1 + 1);
						responseCode = line.substring(blank1 + 1, blank2);
						responseMessage = line.substring(blank2 + 1);
					} else {
						pos = line.indexOf(": ");
						responseHeaders.addProperty(line.substring(0, pos), line.substring(pos + 2));
					}
				}
			}

			if (responseCode.startsWith("30") && !responseCode.equalsIgnoreCase("300")) {
				String newURL = responseHeaders.getPropertyValue("Location");
				if (null == newURL || newURL.length() == 0) {
					throw new IOException();
				}
				is.close();
				parseURL(newURL);
				fetchingURL = true;
			} else {
				fetchingURL = false;
			}
		}

//		if (Adserver.DEFAULT_HTML.equalsIgnoreCase(url) || Adserver.DEFAULT_IMG.equalsIgnoreCase(url)
//				|| Adserver.DEFAULT_IMG_OS5.equalsIgnoreCase(url)) {
			this.is = new AdserverInputStream(is);
//		}

		// TODO TEST Disabled cache mode
		//        if (!cacheMode) {
		//            headersLine.append("\r\n");
		//            this.is = new AdserverInputStream(is, getURL(), headersLine.toString(), application);
		//        } else {
		//            this.is = new AdserverInputStream(is);
		//        }
	}

	/**
	 * Load response from network connection
	 * 
	 * @param filepath
	 *            Path to resource
	 * @param application
	 * @return cahed URL content stream
	 * @throws IOException
	 */
	// TODO TEST Disabled cache mode
	//    private static InputStream buildCacheMode(final String filepath, final AdserverBase application) throws IOException {
	//        FileConnection conn = null;
	//        try {
	//            conn = (FileConnection) Connector.open(CacheManager.getInstance().getCachepath() + application.getHashId() + '/' + filepath, Connector.READ_WRITE);
	//            if (!conn.exists()) {
	//                throw new IOException();
	//            }
	//            return conn.openInputStream();
	//        } finally {
	//            if (null != conn) {
	//                conn.close();
	//            }
	//        }
	//    }

	/**
	 * Parse URL
	 * 
	 * @param value
	 */
	private void parseURL(final String value) {
		String url = value.trim();
		if (url.startsWith("http://")) {
			url = url.substring(7);
		} else if (url.startsWith("https://")) {
			url = url.substring(8);
		}

		String splitter = ":";
		if (url.indexOf(splitter) == -1) {
			splitter = "/";
			if (url.indexOf(splitter) == -1) {
				splitter = "?";
				if (url.indexOf(splitter) == -1) {
					splitter = "";
				}
			}
			this.host = url.substring(0, url.indexOf(splitter));
			this.path = url.substring(this.host.length() + splitter.length() - 1);
			this.host += ":80";
		} else {
			this.host = url.substring(0, url.indexOf(splitter));
			this.path = url.substring(this.host.length() + splitter.length());

			int pathPos = this.host.length() + splitter.length();

			int pos = 0;
			while (this.path.charAt(pos) >= '0' && this.path.charAt(pos) <= '9') {
				pos++;
			}
			this.host += ':' + url.substring(pathPos, pathPos + pos);
			this.path = url.substring(pathPos + pos);
		}

	}

	/**
	 * Parse response string
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String parseHeaderLine(final InputStream is) throws IOException {
		int ch1;

		StringBuffer headerLine = new StringBuffer(70);
		while (true) {
			ch1 = is.read();
			if ('\r' != ch1) {
				if ('\n' == ch1) {
					throw new IOException();
				}
				headerLine.append((char) ch1);
			} else {
				ch1 = is.read();
				if ('\n' == ch1) {
					return headerLine.toString();
				} else {
					throw new IOException();
				}
			}
		}
	}

	public InputStream openInputStream() throws java.io.IOException {
		return this.is;
	}

	public DataInputStream openDataInputStream() throws java.io.IOException {
		return new DataInputStream(this.is);
	}

	public void close() throws java.io.IOException {
		is.close();
	}

	public java.lang.String getURL() {
		return "http://" + host + path;
	}

	public java.lang.String getProtocol() {
		return "HTTP/1.1.";
	}

	public java.lang.String getHost() {
		return host;
	}

	public java.lang.String getFile() {
		return "";
	}

	public java.lang.String getRef() {
		return "";
	}

	public java.lang.String getQuery() {
		return path.substring(path.indexOf('?') + 1);
	}

	public int getPort() {
		return 80;
	}

	public java.lang.String getRequestMethod() {
		return "GET";
	}

	public void setRequestMethod(java.lang.String s) throws java.io.IOException {
	}

	public java.lang.String getRequestProperty(java.lang.String s) {
		if ("referer".equalsIgnoreCase(s)) {
			return "referer";
		} else {
			return headers.getPropertyValue(s);
		}
	}

	public void setRequestProperty(java.lang.String s, java.lang.String s1) throws java.io.IOException {
	}

	public int getResponseCode() throws java.io.IOException {
		try {
			return Integer.parseInt(responseCode);
		} catch (NumberFormatException e) {
			return 404;
		}
	}

	public java.lang.String getResponseMessage() throws java.io.IOException {
		return responseMessage;
	}

	public long getExpiration() throws java.io.IOException {
		try {
			return Long.parseLong(responseHeaders.getPropertyValue("Expiration"));
		} catch (NumberFormatException e) {
			return System.currentTimeMillis();
		}
	}

	public long getDate() throws java.io.IOException {
		return System.currentTimeMillis();
	}

	public long getLastModified() throws java.io.IOException {
		try {
			return Long.parseLong(responseHeaders.getPropertyValue("Last-Modified"));
		} catch (NumberFormatException e) {
			return System.currentTimeMillis();
		}
	}

	public java.lang.String getHeaderField(java.lang.String s) throws java.io.IOException {
		return responseHeaders.getPropertyValue(s);
	}

	public int getHeaderFieldInt(java.lang.String s, int i) throws java.io.IOException {
		try {
			return Integer.parseInt(responseHeaders.getPropertyValue(s));
		} catch (NumberFormatException e) {
			return i;
		}
	}

	public long getHeaderFieldDate(java.lang.String s, long l) throws java.io.IOException {
		try {
			return Long.parseLong(responseHeaders.getPropertyValue(s));
		} catch (NumberFormatException e) {
			return l;
		}
	}

	public java.lang.String getHeaderField(int i) throws java.io.IOException {
		return responseHeaders.getPropertyValue(i);
	}

	public java.lang.String getHeaderFieldKey(int i) throws java.io.IOException {
		return responseHeaders.getPropertyKey(i);
	}

	public java.io.OutputStream openOutputStream() throws java.io.IOException {
		return socket.openOutputStream();
	}

	public java.io.DataOutputStream openDataOutputStream() throws java.io.IOException {
		return socket.openDataOutputStream();
	}

	public java.lang.String getType() {
		return responseHeaders.getPropertyValue("Content-Type");
	}

	public java.lang.String getEncoding() {
		return responseHeaders.getPropertyValue("Encoding");
	}

	public long getLength() {
		try {
			return Integer.parseInt(responseHeaders.getPropertyValue("Content-Length"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
