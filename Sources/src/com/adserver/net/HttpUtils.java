package com.adserver.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANInfo;

public class HttpUtils {
	public static class Cookies {
		public String cookie;
	}

	public static final int defaultMaxLen = 16384;

	static public String toURLArgs(Hashtable ht) {
		StringBuffer args = new StringBuffer();
		Enumeration keys = ht.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			Object value = ht.get(key);

			if (null != key && key.length() > 0 && null != value) {
				if (value instanceof String) {
					args.append(key).append('=')
							.append(encodeURL((String) value)).append('&');
				} else if (value instanceof String[]) {
					String[] values = (String[]) value;
					for (int i = 0; i < values.length; i++) {
						args.append(key).append(i).append('=')
								.append(encodeURL(values[i])).append('&');
					}
				}
			} else if (null != key && key.length() == 0 && null != value) {
				if (value instanceof String) {
					args.append(value);
				}
			}
			// if (value instanceof String) {
			// args.append(key).append('=').append(encodeURL((String)
			// value)).append('&');
			// } else if (value != null) {
			// String[] values = (String[]) value;
			// for (int i = 0; i < values.length; i++)
			// args.append(key).append(i).append('=').append(encodeURL(values[i])).append('&');
			// }
		}
		return args.toString();
	}

	static public String post(Hashtable ht, String url) {
		return post(ht, url, defaultMaxLen);
	}

	static public String post(Hashtable ht, String url, int maxlen) {
		return post(ht, url, maxlen, null);
	}

	static public String post(Hashtable ht, String url, int maxlen,
			Cookies cookies) {
		HttpConnection con = null;
		InputStream is = null;
		OutputStream os = null;
		int redirects = 0;
		int rc;
		if (cookies == null)
			cookies = new Cookies();

		try {
			while (true) {
				ConnectionIterator iterator = getConnectionIterator(url);
				while (true) {
					try {
						con = iterator.getNextConnection();
						if (con == null)
							continue;
						if (cookies.cookie != null)
							con.setRequestProperty("Cookie", cookies.cookie);
						String _post = toURLArgs(ht);
						byte[] post = _post.getBytes();
						// _post = null;
						con.setRequestMethod(HttpConnection.POST);
						con.setRequestProperty("User-Agent", "BlackBerry");
						con.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
						con.setRequestProperty("Content-Length",
								String.valueOf(post.length));
						os = con.openOutputStream();
						os.write(post);
						os.flush();
						is = con.openInputStream();
						rc = con.getResponseCode();
						break;
					} catch (IOException e) {
						// Try another transport
					} catch (NoMoreTransportsException e) {
						throw new IOException("No internet connection");
					}
				}
				String cookie = con.getHeaderField("Set-Cookie");
				if (cookie != null)
					cookies.cookie = cookie;
				if (rc == HttpConnection.HTTP_TEMP_REDIRECT
						|| rc == HttpConnection.HTTP_SEE_OTHER
						|| rc == HttpConnection.HTTP_MOVED_PERM
						|| rc == HttpConnection.HTTP_MOVED_TEMP) {
					String loc = con.getHeaderField("Location");
					url = genRedirURL(url, loc);
					if (url.length() > 4 && redirects++ < 10) {
						try {
							if (is != null)
								is.close();
						} catch (Exception e) {
							// Ignore
						}
						try {
							if (con != null)
								con.close();
						} catch (Exception e) {
							// Ignore
						}
						continue;
					}
				}
				if (rc != HttpConnection.HTTP_OK)
					throw new IOException("rc = " + rc + " for " + url);
				break;
			}
			byte[] bs = new byte[1024];
			StringBuffer resp = new StringBuffer();
			int total = 0, len;
			while ((len = is.read(bs, 0, Math.min(bs.length, maxlen - total))) > 0
					&& total < maxlen) {
				resp.append(new String(bs, 0, len));
				total += len;
			}
			return resp.toString().trim();
		} catch (Exception e) {
			// TODO
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (Exception ignored) {
			}
			try {
				if (is != null)
					is.close();
			} catch (Exception ignored) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception ignored) {
			}
		}
		return "EXC";
	}

	static public String get(String url) {
		return get(url, defaultMaxLen);
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

	static public String get(String url, int maxlen) {
		return get(url, maxlen, null);
	}
	
	static public HttpConnection getConnection(String url) throws IOException {
		HttpConnection con = null;
		int rc;
		int redirects = 0;
		
		while (true) {
			ConnectionIterator iterator = getConnectionIterator(url);
			while (true) {
				try {
					con = iterator.getNextConnection();
					if (con == null)
						continue;
					rc = con.getResponseCode();
					break;
				} catch (IOException e) {
					// Try another transport
				} catch (NoMoreTransportsException e) {
					throw new IOException("No internet connection");
				}
			}
			if (rc == HttpConnection.HTTP_TEMP_REDIRECT
					|| rc == HttpConnection.HTTP_SEE_OTHER
					|| rc == HttpConnection.HTTP_MOVED_PERM
					|| rc == HttpConnection.HTTP_MOVED_TEMP) {
				String loc = con.getHeaderField("Location");
				url = genRedirURL(url, loc);
				if (url.length() > 4 && redirects++ < 10) {
					try {
						if (con != null)
							con.close();
					} catch (Exception ignored) {
					}
					continue;
				}
			}
			if (rc != HttpConnection.HTTP_OK)
				throw new IOException("No internet connection");
			break;
		}
		
		return con;
	}

	static public String get(String url, int maxlen, Cookies cookies) {
		HttpConnection con = null;
		InputStream is = null;
		int redirects = 0;
		int rc;
		if (cookies == null)
			cookies = new Cookies();
		try {
			while (true) {
				ConnectionIterator iterator = getConnectionIterator(url);
				// c.log("GET: got iterator");
				while (true) {
					try {
						con = iterator.getNextConnection();
						// c.log("GET: trying next connection");
						if (con == null)
							continue;
						if (cookies.cookie != null)
							con.setRequestProperty("Cookie", cookies.cookie);
						is = con.openInputStream();
						rc = con.getResponseCode();
						break;
					} catch (IOException e) {
						// Try another transport
					} catch (NoMoreTransportsException e) {
						throw new IOException("No internet connection");
					}
				}
				String cookie = con.getHeaderField("Set-Cookie");
				if (cookie != null)
					cookies.cookie = cookie;
				if (rc == HttpConnection.HTTP_TEMP_REDIRECT
						|| rc == HttpConnection.HTTP_SEE_OTHER
						|| rc == HttpConnection.HTTP_MOVED_PERM
						|| rc == HttpConnection.HTTP_MOVED_TEMP) {
					String loc = con.getHeaderField("Location");
					url = genRedirURL(url, loc);
					if (url.length() > 4 && redirects++ < 10) {
						try {
							if (is != null)
								is.close();
						} catch (Exception ignored) {
						}
						try {
							if (con != null)
								con.close();
						} catch (Exception ignored) {
						}
						continue;
					}
				}
				if (rc != HttpConnection.HTTP_OK)
					throw new IOException("rc = " + rc + " for " + url);
				break;
			}
			byte[] bs = readStream(is);
			/*
			 * byte[] bs = new byte[1024]; StringBuffer resp = new
			 * StringBuffer(); int total = 0, len; while ((len = is.read(bs, 0,
			 * Math.min(bs.length, maxlen - total))) > 0 && total < maxlen) {
			 * resp.append(new String(bs, 0, len)); total += len; c.log(total +
			 * " " + url); c.log(resp.toString()); }
			 */

			String response = new String(bs, "UTF-8");
			return response.trim();
			// return resp.toString().trim();
		} catch (Exception e) {
			// TODO
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				// TODO
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				// TODO
			}
		}
		return "";
	}

	public static byte[] readStream(InputStream in) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int c;
			try {
				c = in.read();
			} catch (Exception ex) {
				c = -1;
			}

			while (c >= 0) {
				baos.write(c);
				try {
					c = in.read();
				} catch (Exception ex) {
					c = -1;
				}
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		}
	}

	static public byte[] download(String url) {
		HttpConnection con = null;
		InputStream is = null;
		int redirects = 0, rc;
		Cookies cookies = new Cookies();

		try {
			while (true) {
				ConnectionIterator iterator = getConnectionIterator(url);
				while (true) {
					try {
						con = iterator.getNextConnection();
						if (con == null)
							continue;
						if (cookies.cookie != null)
							con.setRequestProperty("Cookie", cookies.cookie);
						is = con.openInputStream();
						rc = con.getResponseCode();
						break;
					} catch (IOException e) {
						// Try another transport
					} catch (NoMoreTransportsException e) {
						throw new IOException("No internet connection");
					}
				}
				String cookie = con.getHeaderField("Set-Cookie");
				if (cookie != null)
					cookies.cookie = cookie;
				if (rc == HttpConnection.HTTP_TEMP_REDIRECT
						|| rc == HttpConnection.HTTP_SEE_OTHER
						|| rc == HttpConnection.HTTP_MOVED_PERM
						|| rc == HttpConnection.HTTP_MOVED_TEMP) {
					String loc = con.getHeaderField("Location");
					url = genRedirURL(url, loc);
					if (url.length() > 4 && redirects++ < 10) {
						try {
							if (is != null)
								is.close();
						} catch (Exception ignored) {
						}
						try {
							if (con != null)
								con.close();
						} catch (Exception ignored) {
						}
						continue;
					}
				}
				if (rc != HttpConnection.HTTP_OK)
					throw new IOException("rc = " + rc + " for " + url);
				break;
			}
			ByteArrayOutputStream bso = new ByteArrayOutputStream();
			byte[] bs = new byte[1024];
			int len;
			int total = 0;
			while ((len = is.read(bs, 0, bs.length)) > 0) {
				bso.write(bs, 0, len);
				total += len;
			}
			return bso.toByteArray();
		} catch (Exception e) {
			// TODO
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				// TODO
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				// TODO
			}
		}
		return null;
	}

	// Unreserved punctuation mark/symbols
	private static String mark = "-_.!~*'()\"";

	/**
	 * Converts Hex digit to a UTF-8 "Hex" character
	 * 
	 * @param digitValue
	 *            digit to convert to Hex
	 * @return the converted Hex digit
	 */
	static private char toHexChar(int digitValue) {
		if (digitValue < 10)
			// Convert value 0-9 to char 0-9 hex char
			return (char) ('0' + digitValue);
		else
			// Convert value 10-15 to A-F hex char
			return (char) ('A' + (digitValue - 10));
	}

	/**
	 * Encodes a URL - This method assumes UTF-8
	 * 
	 * @param url
	 *            URL to encode
	 * @return the encoded URL
	 */
	static public String encodeURL(String url) {
		StringBuffer encodedUrl = new StringBuffer(); // Encoded URL
		int len = url.length();
		// Encode each URL character
		for (int i = 0; i < len; i++) {
			char c = url.charAt(i); // Get next character
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z'))
				// Alphanumeric characters require no encoding, append as is
				encodedUrl.append(c);
			else {
				int imark = mark.indexOf(c);
				if (imark >= 0) {
					// Unreserved punctuation marks and symbols require
					// no encoding, append as is
					encodedUrl.append(c);
				} else {
					// Encode all other characters to Hex, using the format
					// "%XX",
					// where XX are the hex digits
					encodedUrl.append('%'); // Add % character
					// Encode the character's high-order nibble to Hex
					encodedUrl.append(toHexChar((c & 0xF0) >> 4));
					// Encode the character's low-order nibble to Hex
					encodedUrl.append(toHexChar(c & 0x0F));
				}
			}
		}
		return encodedUrl.toString(); // Return encoded URL
	}

	static final String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";

	public static String doMultipartPost(String url, Hashtable params,
			String fileField, String fileName, String fileType, byte[] fileBytes)
			throws Exception {
		String boundary = getBoundaryString();
		String boundaryMessage = getBoundaryMessage(boundary, params,
				fileField, fileName, fileType);
		String endBoundary = "\r\n--" + boundary + "--\r\n";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(boundaryMessage.getBytes());
		bos.write(fileBytes);
		bos.write(endBoundary.getBytes());
		byte[] mpPostBytes = bos.toByteArray();
		bos.close();

		HttpConnection hc = null;
		InputStream is = null;
		bos = new ByteArrayOutputStream();
		byte[] res = null;

		try {
			hc = (HttpConnection) Connector.open(url + getConnectionSuffix());
			hc.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + getBoundaryString());
			hc.setRequestMethod(HttpConnection.POST);
			OutputStream dout = hc.openOutputStream();
			dout.write(mpPostBytes);
			dout.close();
			int ch;

			is = hc.openInputStream();
			while ((ch = is.read()) != -1)
				bos.write(ch);
			res = bos.toByteArray();
		} catch (Exception e) {
			// TODO
		} finally {
			try {
				if (bos != null)
					bos.close();
			} catch (Exception ignored) {
			}
			try {
				if (is != null)
					is.close();
			} catch (Exception ignored) {
			}
			try {
				if (hc != null)
					hc.close();
			} catch (Exception ignored) {
			}
		}
		return new String(res).trim();
	}

	private static String getBoundaryString() {
		return BOUNDARY;
	}

	private static String getBoundaryMessage(String boundary, Hashtable params,
			String fileField, String fileName, String fileType) {
		StringBuffer res = new StringBuffer("--").append(boundary).append(
				"\r\n");
		Enumeration keys = params.keys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) params.get(key);

			res.append("Content-Disposition: form-data; name=\"").append(key)
					.append("\"\r\n").append("\r\n").append(value)
					.append("\r\n").append("--").append(boundary)
					.append("\r\n");
		}
		res.append("Content-Disposition: form-data; name=\"").append(fileField)
				.append("\"; filename=\"").append(fileName).append("\"\r\n")
				.append("Content-Type: ").append(fileType).append("\r\n\r\n");

		return res.toString();
	}

	public static String scrape(String resp, String start, String stop) {
		int offset, len;
		if ((offset = resp.indexOf(start)) < 0) {
			return "";
		}

		if ((len = resp.indexOf(stop, offset + start.length())) < 0) {
			return "";
		}

		return resp.substring(offset + start.length(), len);
	}

	public static int stringLastIndexOf(String haystack, String needle) {
		int index, lastIndex = -1;
		while ((index = haystack.indexOf(needle, lastIndex + 1)) >= 0)
			lastIndex = index;
		return lastIndex;
	}

	public static String getConnectionSuffix() {
		String connSuffix;
		if (DeviceInfo.isSimulator()) {
			connSuffix = ";deviceside=true";
		} else if ((WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED)
				&& RadioInfo.areWAFsSupported(RadioInfo.WAF_WLAN)) {
			connSuffix = ";interface=wifi";
		} else {
			String uid = null;
			ServiceBook sb = ServiceBook.getSB();
			ServiceRecord[] records = sb.findRecordsByCid("WPTCP");
			for (int i = 0; i < records.length; i++) {
				if (records[i].isValid() && !records[i].isDisabled()) {
					if (records[i].getUid() != null
							&& records[i].getUid().length() != 0) {
						if ((records[i].getCid().toLowerCase().indexOf("wptcp") != -1)
								&& (records[i].getUid().toLowerCase()
										.indexOf("wifi") == -1)
								&& (records[i].getUid().toLowerCase()
										.indexOf("mms") == -1)) {
							uid = records[i].getUid();
							break;
						}
					}
				}
			}
			if (uid != null) {
				// WAP2 Connection
				connSuffix = ";ConnectionUID=" + uid;
			} else {
				connSuffix = ";deviceside=true";
			}
		}
		return connSuffix + ";ConnectionTimeout=20000";
	}

	private static ConnectionIterator getConnectionIterator(String url) {
		return new HttpConnectionFactory(url, ";ConnectionTimeout=20000");
	}
}
