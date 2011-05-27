package com.adserver.core;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;

/**
 * User for determine GPS location <br>
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */

public class LocationManagerV2 {

	private Thread					thread					= null;
	private Coordinates				coordinates				= null;
	private static LocationManagerV2	instance				= null;

	/**
	 * Constructor
	 */
	private LocationManagerV2() {
		startLoading();
	}

	public synchronized static LocationManagerV2 getInstance() {
		if (null == instance) {
			instance = new LocationManagerV2();
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
			thread.start();
		}
	}
	
	private synchronized void loadCoordinates() {
		try {
			Criteria c = new Criteria();
			c.setCostAllowed(false);
			LocationProvider lp = LocationProvider.getInstance(c);
			if (null != lp) {
				Location loc = lp.getLocation(-1);
				setCoordinates(loc.getQualifiedCoordinates());
			}
		} catch (Exception ignored) {
		}
	}

	private synchronized void setCoordinates(final Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public synchronized Coordinates getCoordinates() {
		return coordinates;
	}
}
