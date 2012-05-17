package com.MASTAdview.core;


import javax.microedition.location.Coordinates;

import com.MASTAdview.utils.LocationManager;
import com.MASTAdview.utils.Utils;

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
	public static volatile String longitude = null;
	public static volatile String latitude = null;
	public static volatile boolean isFetchingCoordinates = false;
	private boolean locationDetection;
	private int locationMinWaitMillis;
	
	
	private AutoDetectParameters() {
			instance = this;
			userAgent = System.getProperty("browser.useragent");
			deviceId = Integer.toString(DeviceInfo.getDeviceId());
			md5DeviceId = Utils.getMD5Hash(deviceId);
			country = Locale.getDefault().getCountry();
			carrier =  RadioInfo.getCurrentNetworkName();
			locationDetection = false;
			locationMinWaitMillis = 5 * 60 * 1000;	// 5 minutes (in millis)
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
	
	public boolean getLocationDetection()
	{
		return locationDetection;
	}
	
	public void setLocationDetection(boolean value)
	{
		locationDetection = value;
	}
	
	public int getLocationMinWaitMillis()
	{
		return locationMinWaitMillis;
	}
	
	public void setLocationMinWaitMillis(int value)
	{
		locationMinWaitMillis = value;
	}
	
	public String getLongitude() {
		if (null != longitude) {
			return longitude;
		} else if (isFetchingCoordinates) {
			return null;
		} else {
			if (locationDetection)
			{
				Coordinates coords = LocationManager.getInstance(locationMinWaitMillis).getCoordinates();
				return null != coords ? String.valueOf(coords.getLongitude()) : null;
			}
			else
			{
				return null;
			}
		}
	}

	public String getLatitude() {
		if (null != latitude) {
			return latitude;
		} else if (isFetchingCoordinates) {
			return null;
		} else {
			if (locationDetection)
			{
				Coordinates coords = LocationManager.getInstance(locationMinWaitMillis).getCoordinates();
				return null != coords ? String.valueOf(coords.getLatitude()) : null;
			}
			else
			{
				return null;
			}
		}
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
