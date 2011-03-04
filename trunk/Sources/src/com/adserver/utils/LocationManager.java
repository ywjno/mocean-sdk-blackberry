package com.adserver.utils;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;

/**
 * User for determine GPS location <br>
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class LocationManager {

	public static final int			GPS_LOCATION_TIMEOUT	= 60;

	private Thread					thread					= null;
	private Coordinates				coordinates				= null;
	private static LocationManager	instance				= null;

	/**
	 * Constructor
	 */
	private LocationManager() {
	}

	public synchronized static LocationManager getInstance() {
		if (null == instance) {
			instance = new LocationManager();
		}
		return instance;
	}

	private synchronized void startLoading() {
		if (null == thread || !thread.isAlive()) {
			thread = new Thread() {
				public void run() {
					loadCoordinates();
				}
			};
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}

	private synchronized void loadCoordinates() {
		try {
			LocationProvider lp = LocationProvider.getInstance(getCriteria());
			if (null != lp) {
				Location loc = lp.getLocation(GPS_LOCATION_TIMEOUT);
				setCoordinates(loc.getQualifiedCoordinates());
			}
		} catch (Exception ignored) {
		}
	}

	private synchronized Criteria getCriteria() {
		Criteria result = new Criteria();
		result.setHorizontalAccuracy(500);
		result.setVerticalAccuracy(500);
		result.setPreferredResponseTime(GPS_LOCATION_TIMEOUT * 1000);
		return result;
	}

	private synchronized void setCoordinates(final Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public synchronized Coordinates getCoordinates() {
		startLoading();
		return coordinates;
	}
}
