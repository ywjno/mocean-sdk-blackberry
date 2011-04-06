package com.adserver.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Application;

import com.adserver.core.AdserverBase.AdserverNoNetworkNotify;
import com.adserver.net.HttpUtils;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class AdserverConnection implements HttpConnection {
	private HttpConnection	connection;
	private AdserverBase	application;
	private String			originalUrl	= "";
	private byte[] 			saveBuffer = null;
	private boolean		reUse = false;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            Request URL
	 * @param application
	 * @throws IOException
	 */
	public AdserverConnection(final String url, final AdserverBase application) throws IOException {
		this.originalUrl = url;
		this.application = application;
		connection = HttpUtils.getConnection(url);
		// connection = (HttpConnection) Connector.open(url/* + " " +
		// getConnectionString()*/, Connector.READ_WRITE, true);
	}

	public InputStream openInputStream() throws java.io.IOException {
		if (!reUse) {
			InputStream tmp = connection.openInputStream();
			byte[] data = IOUtilities.streamToBytes(tmp);
			saveBuffer = data;
			return new ByteArrayInputStream(data);
		} else {
			setReuse(false);
			return new ByteArrayInputStream(saveBuffer);
		}
	}



	public DataInputStream openDataInputStream() throws java.io.IOException {
		return connection.openDataInputStream();
	}

	public void close() throws java.io.IOException {
		connection.close();
	}

	public java.lang.String getURL() {
		return connection.getURL();
	}

	public java.lang.String getProtocol() {
		return connection.getProtocol();
	}

	public java.lang.String getHost() {
		return connection.getHost();
	}

	public java.lang.String getFile() {
		return connection.getFile();
	}

	public java.lang.String getRef() {
		return connection.getRef();
	}

	public java.lang.String getQuery() {
		return connection.getQuery();
	}

	public int getPort() {
		return connection.getPort();
	}

	public java.lang.String getRequestMethod() {
		return connection.getRequestMethod();
	}

	public void setRequestMethod(java.lang.String s) throws java.io.IOException {
		connection.setRequestMethod(s);
	}

	public java.lang.String getRequestProperty(java.lang.String s) {
		return connection.getRequestProperty(s);
	}

	public void setRequestProperty(java.lang.String s, java.lang.String s1) throws java.io.IOException {
		connection.setRequestProperty(s, s1);
	}

	public int getResponseCode() throws java.io.IOException {
		return connection.getResponseCode();
	}

	public java.lang.String getResponseMessage() throws java.io.IOException {
		return connection.getResponseMessage();
	}

	public long getExpiration() throws java.io.IOException {
		return connection.getExpiration();
	}

	public long getDate() throws java.io.IOException {
		return connection.getDate();
	}

	public long getLastModified() throws java.io.IOException {
		return connection.getLastModified();
	}

	public java.lang.String getHeaderField(java.lang.String s) throws java.io.IOException {
		return connection.getHeaderField(s);
	}

	public int getHeaderFieldInt(java.lang.String s, int i) throws java.io.IOException {
		return connection.getHeaderFieldInt(s, i);
	}

	public long getHeaderFieldDate(java.lang.String s, long l) throws java.io.IOException {
		return connection.getHeaderFieldDate(s, l);
	}

	public java.lang.String getHeaderField(int i) throws java.io.IOException {
		return connection.getHeaderField(i);
	}

	public java.lang.String getHeaderFieldKey(int i) throws java.io.IOException {
		return connection.getHeaderFieldKey(i);
	}

	public java.io.OutputStream openOutputStream() throws java.io.IOException {
		return connection.openOutputStream();
	}

	public java.io.DataOutputStream openDataOutputStream() throws java.io.IOException {
		return connection.openDataOutputStream();
	}

	public java.lang.String getType() {
		return connection.getType();
	}

	public java.lang.String getEncoding() {
		return connection.getEncoding();
	}

	public long getLength() {
		return connection.getLength();
	}

	/**
	 * Determines what connection type to use and returns the necessary string to use it.
	 * 
	 * @return A string with the connection info
	 * @throws java.io.IOException
	 */
	/*
	 * public static String getConnectionString() throws IOException { // Wifi
	 * is the preferred transmission method if ((RadioInfo.getActiveWAFs() &
	 * RadioInfo.WAF_WLAN) != 0) { return ";interface=wifi"; }
	 * 
	 * // Is the carrier network the only way to connect? // 1 equals
	 * CoverageInfo.COVERAGE_CARRIER on 4.2-4.3 and CoverageInfo.COVERAGE_DIRECT
	 * on 4.5-4.7 else if ((CoverageInfo.getCoverageStatus() & 1) == 1) { String
	 * carrierUid = getCarrierBIBSUid(); if (null == carrierUid) { // Has
	 * carrier coverage, but not BIBS. So use the carrier's TCP network return
	 * ";deviceside=true"; } else { // otherwise, use the Uid to construct a
	 * valid carrier BIBS request return ";deviceside=false;connectionUID=" +
	 * carrierUid + ";ConnectionType=mds-public"; } }
	 * 
	 * // Check for an MDS connection instead (BlackBerry Enterprise Server)
	 * else if ((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_MDS)
	 * == CoverageInfo.COVERAGE_MDS) { return ";deviceside=false"; }
	 * 
	 * // If there is no connection available abort to avoid bugging the user
	 * unnecssarily. else if (CoverageInfo.getCoverageStatus() ==
	 * CoverageInfo.COVERAGE_NONE) { throw new IOException(); }
	 * 
	 * // In theory, all bases are covered so this shouldn't be reachable. else
	 * { return ";deviceside=true"; } }
	 */

	/**
	 * Looks through the phone's service book for a carrier provided BIBS network
	 * 
	 * @return The uid used to connect to that network.
	 */
	/*
	 * private static String getCarrierBIBSUid() { ServiceRecord[] records =
	 * ServiceBook.getSB().getRecords(); int currentRecord;
	 * 
	 * for (currentRecord = 0; currentRecord < records.length; currentRecord++)
	 * { if (records[currentRecord].getCid().toLowerCase().equals("ippp")) { if
	 * (records[currentRecord].getName().toLowerCase().indexOf("bibs") >= 0) {
	 * return records[currentRecord].getUid(); } } }
	 * 
	 * return null; }
	 */

	// TODO TEST Disabled cache mode
	// private void saveCache(byte[] data) {
	// String root = CacheManager.getInstance().getCachepath() +
	// application.getHashId() + '/' + CacheManager.getMD5Hash(originalUrl);
	// FileConnection conn = null;
	// OutputStream os = null;
	// try {
	// try {
	// conn = (FileConnection) Connector.open(root, Connector.READ_WRITE);
	// if (!conn.exists()) {
	// conn.create();
	// }
	// os = conn.openOutputStream();
	// os.write(getHeadersLine().getBytes("UTF-8"));
	// os.write(data);
	// } finally {
	// if (null != conn) {
	// conn.close();
	// }
	// if (null != os) {
	// os.close();
	// }
	// }
	// } catch (IOException ignored) {
	// }
	// }

	/*
	 * private String getHeadersLine() { StringBuffer result = new
	 * StringBuffer();
	 * 
	 * String[] keys = new String[]{"Server", "X-Powered-By", "Content-Type",
	 * "Content-Length", "Connection"}; try { for (int i = 0; i < keys.length;
	 * ++i) { if (getHeaderField(keys[i]) != null) {
	 * result.append(keys[i]).append
	 * (": ").append(getHeaderField(keys[i])).append("\r\n"); } } } catch
	 * (IOException ignored) { } result.append("\r\n"); return
	 * result.toString(); }
	 */
	public void setReuse(boolean reUse) {
		this.reUse = reUse;
	}
	
	public void setBufferContent(String data) {
		this.saveBuffer = data.getBytes();
	}
}
