package com.MASTAdview.utils;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
import com.MASTAdview.core.AutoDetectParameters;

/**
 * User for determine GPS location <br>
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class LocationManager {

    //public static final int GPS_LOCATION_TIMEOUT = 180;

	//private Thread					thread					= null;
	private Coordinates				coordinates				= null;
	private static LocationManager	instance				= null;
	private LocationProvider 		lp						= null;
	private int 				locationUpdateInterval;
	
	
	/**
	 * Constructor
	 */
	private LocationManager(int updateInterval) {
		//startLoading();
		locationUpdateInterval = updateInterval;
		ListenForLocations(updateInterval);
	}

	public synchronized static LocationManager getInstance(int updateInterval) {
		if (null == instance) {
			instance = new LocationManager(updateInterval);
		}
		return instance;
	}

	/*
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
	 */
	
	private boolean ListenForLocations(int interval)
	{
		boolean retval = false;
		AutoDetectParameters.isFetchingCoordinates = true;
		
		try
		{
			Criteria cr = new Criteria();
	        cr.setHorizontalAccuracy(10000);
	        cr.setVerticalAccuracy(10000);
	        cr.setPreferredResponseTime(60000);

	        lp = LocationProvider.getInstance(cr);
 			if (lp != null)
			{
				// Only a single listener can be associated with a provider, and
				// unsetting it involves the same call but with null, therefore no
				// need to cache the listener instance request an update every second
				lp.setLocationListener(new LocationListenerImpl(), interval, 1, 1);
				retval = true;
			}
		} 
		catch (Exception ex)
		{
			// Log error
			retval = false;
		}

		return retval;
	}
	
	
	private class LocationListenerImpl implements LocationListener 
	{
		synchronized public void locationUpdated(LocationProvider provider, Location location)
		{
			QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
			if (null != coordinates) {
				AutoDetectParameters.latitude  = String.valueOf(coordinates.getLatitude());
		        AutoDetectParameters.longitude = String.valueOf(coordinates.getLongitude());
			}
	        AutoDetectParameters.isFetchingCoordinates = false;
	        if (locationUpdateInterval < 0)
	        {
	        	stopLoading(); // one time update only
	        }
		}

		public void providerStateChanged(LocationProvider provider, int newState)
		{
			// NA		
		}
	}

	
//	private synchronized void setCoordinates(final Coordinates coordinates) {
//		this.coordinates = coordinates;
//	}

	public synchronized Coordinates getCoordinates() {
//		startLoading();
		return coordinates;
	}

	public synchronized void stopLoading()
	{
		try
		{
			//thread.interrupt();
			if (lp != null)
			{
				lp.reset();
				lp.setLocationListener(null, -1, -1, -1);
				lp = null;
			}
			AutoDetectParameters.isFetchingCoordinates = false;
		}
		catch (Exception e) 
		{
		}
	}
}
