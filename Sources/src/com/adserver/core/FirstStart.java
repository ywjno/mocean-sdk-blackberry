package com.adserver.core;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import com.adserver.net.HttpUtils;
import com.adserver.utils.URLParamEncoder;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
public class FirstStart extends Thread {
	private final static String URL = "http://www.moceanmobile.com/appconversion.php?";
	private final static long STORAGE_ID = 0xE984EDEF6F11B8C8L;
	private final static PersistentObject store = PersistentStore
			.getPersistentObject(STORAGE_ID);

	String advertiserId = "";
	String groupCode = "";

	public FirstStart(String advertiserId, String groupCode) {
		super();

		this.advertiserId = advertiserId;
		this.groupCode = groupCode;

		setPriority(Thread.MIN_PRIORITY);
		if (!checkFirstStart()) {
			start();
		}
	}

	private static void markFirstStart() {
		synchronized (store) {
			store.setContents(new Long(STORAGE_ID));
			store.commit();
		}
	}

	private static boolean checkFirstStart() {
		synchronized (store) {
			Object storage = store.getContents();
			return storage instanceof Long
					&& ((Long) storage).longValue() == STORAGE_ID;
		}
	}

	public final void run() {
		System.out.println("Install notification URL = " + URL + getQueryString());
		byte[] result = HttpUtils.download(URL + getQueryString());
		System.out.println("Install notification result " + new String(result));
		if (null != result && result.length > 0 && '0' == (char) result[0]) {
			markFirstStart();
		}

		/*
		 * try { HttpConnection hc = null; try { hc = (HttpConnection)
		 * Connector.open(URL + getQueryString(), Connector.READ_WRITE, true);
		 * hc.setRequestMethod(HttpConnection.GET); if (hc.getResponseCode() ==
		 * HttpConnection.HTTP_OK) { InputStream is = null; try { is =
		 * hc.openInputStream(); int resultCode = is.read(); if ('0' == (char)
		 * resultCode) { markFirstStart(); } } finally { if (null != is) {
		 * is.close(); } } } } finally { if (null != hc) { hc.close(); } } }
		 * catch (IOException ignored) { }
		 */
	}

	private String getQueryString() {
		URLParamEncoder params = new URLParamEncoder();
		params.addParam("advertiser_id", advertiserId);
		params.addParam("group_code", groupCode);
		int deviceId = DeviceInfo.getDeviceId();
		params.addParam("udid", CacheManager.getMD5Hash(Integer.toString(deviceId)));

		return params.toString();
	}
}
