package com.adserver.core;

import java.io.IOException;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import com.adserver.utils.URLParamEncoder;
import com.adserver.utils.Utils;


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
	AdserverBase adserver = null;

	public FirstStart(String advertiserId, String groupCode, AdserverBase adserver) {
		super();

		this.advertiserId = advertiserId;
		this.groupCode = groupCode;
		this.adserver = adserver;

		setPriority(Thread.MIN_PRIORITY);
		if (!checkFirstStart()) {
			adserver.getLogger().info(" >>>>>>>>> Install notification : Preping to mark phone");
			start();
		} else {
			adserver.getLogger().info(" >>>>>>>>> Install notification : Allready marked");
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
//		byte[] result = HttpUtils.download(URL + getQueryString());
		byte[] result = null;
		try {
			result = DataRequest.getResponse(URL+getQueryString()).getBytes();
		}catch (IOException e) {
		}
		
		System.out.println("Install notification result " + new String(result));
		if ((null != result) && (Utils.scrape(new String(result), "<result>", "</result>")).equals("OK")) {
			adserver.getLogger().info(" >>>>>>>>>> Install notification response : OK");
//		if (null != result && result.length > 0 && '0' == (char) result[0]) {
			markFirstStart();
		} else adserver.getLogger().info(" >>>>>>>>>> Install notification response : failed");
	}

	private String getQueryString() {
		URLParamEncoder params = new URLParamEncoder();
		params.addParam("advertiser_id", advertiserId);
		params.addParam("group_code", groupCode);
		params.addParam("udid", AutoDetectParameters.getInstance().getMd5DeviceId());

		return params.toString();
	}
}
