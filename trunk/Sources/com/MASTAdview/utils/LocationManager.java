package com.MASTAdview.utils;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;

import com.MASTAdview.core.AutoDetectParameters;

/**
 * User for determine GPS location <br>
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class LocationManager {

    public static final int GPS_LOCATION_TIMEOUT = 180;

	private Thread					thread					= null;
	private Coordinates				coordinates				= null;
	private static LocationManager	instance				= null;

	/**
	 * Constructor
	 */
	private LocationManager() {
		startLoading();
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
					AutoDetectParameters.isFetchingCoordinates = true;
					coordinates = getGpsLocation();
					if (null != coordinates) {
						AutoDetectParameters.latitude  = String.valueOf(coordinates.getLatitude());
				        AutoDetectParameters.longitude = String.valueOf(coordinates.getLongitude());
					}
			        AutoDetectParameters.isFetchingCoordinates = false;
				}
			};
			thread.start();
		}
	}


	public static Coordinates getGpsLocation() {
        Location loc;
        Coordinates coords;
        LocationProvider lp;
        Criteria cr = new Criteria();
        cr.setHorizontalAccuracy(10000);
        cr.setVerticalAccuracy(10000);
        cr.setPreferredResponseTime(60000);

        try {
            lp = LocationProvider.getInstance(cr);

            if (null == lp) {
                return null;
            } else {
                loc = lp.getLocation(GPS_LOCATION_TIMEOUT);
                coords = loc.getQualifiedCoordinates();
            }
        } catch (Exception e) {
            return null;
        }
        return coords;
    }


//	private synchronized void setCoordinates(final Coordinates coordinates) {
//		this.coordinates = coordinates;
//	}

	public synchronized Coordinates getCoordinates() {
//		startLoading();
		return coordinates;
	}

	public synchronized void stopLoading() {
		try {
			thread.interrupt();
		} catch (Exception e) {
		}
	}
}
