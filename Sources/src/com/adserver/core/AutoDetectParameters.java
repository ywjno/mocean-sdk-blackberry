package com.adserver.core;


import javax.microedition.location.Coordinates;

import com.adserver.utils.LocationManager;
import com.adserver.utils.Utils;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
public class AutoDetectParameters {
	private static AutoDetectParameters instance;
	private static String userAgent = null;
	private static String deviceId = null;
	private static String md5DeviceId = null;
	private static String country = null;
	private static String carrier = null;
	private static String longitude = null;
	private static String latitude = null;
	
	
	private AutoDetectParameters() {
			instance = this;
			userAgent = System.getProperty("browser.useragent");
			deviceId = Integer.toString(DeviceInfo.getDeviceId());
			md5DeviceId = Utils.getMD5Hash(deviceId);
			country = Locale.getDefault().getCountry();
			carrier =  RadioInfo.getCurrentNetworkName();
	}
	
	public static synchronized AutoDetectParameters getInstance() {
		if ( instance == null ) {
			instance = new AutoDetectParameters();
		}
		return instance;
	}

	/**
	 * Returns BlackBerry User-Agent
	 * 
	 * @return User-Agent string
	 */
	public String getUserAgent() {
		return userAgent;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public String getMd5DeviceId() {
		return md5DeviceId;
	}
	
	public String getLongitude() {
		//V2
//		Coordinates coords = LocationManagerV2.getInstance().getCoordinates();
//		if ((null != coords) && (coords.getLongitude() != 0)) {
//			longitude = String.valueOf(coords.getLongitude());
//			return longitude;
//		} else return null;
		//V1
		Coordinates coords = LocationManager.getInstance().getCoordinates();
		return null != coords ? String.valueOf(coords.getLongitude()) : null;
	}

	public String getLatitude() {
		//V2
//		Coordinates coords = LocationManagerV2.getInstance().getCoordinates();
//		if ((null != coords) && (coords.getLatitude() != 0)) {
//			latitude = String.valueOf(coords.getLatitude());
//			return latitude;
//		} else return null;

		//V1
		Coordinates coords = LocationManager.getInstance().getCoordinates();
		return null != coords ? String.valueOf(coords.getLatitude()) : null;
	}
	
	/**
	 * Returns country from current locale - auto-detected
	 * 
	 * @return Current country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Returns current carrier name - auto-detected
	 * 
	 * @return Current carrier name
	 */
	public String getCarrier() {
		return carrier;
	}

}
