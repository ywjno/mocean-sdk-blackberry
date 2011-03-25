package com.adserver.core;

import javax.microedition.location.Coordinates;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;

import com.adserver.utils.LocationManager;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class Adserver extends AdserverBase {
	
	/**
	 * @param appID
	 *            Deprecated.
	 *            Identificator of application
	 * @param mode
	 *            Deprecated.
	 *            Working mode:<br>
	 *            MODE_COUNTER_ONLY - no ads, just counter,<br>
	 *            MODE_ADS_ONLY = only ads, no counter,<br>
	 *            MODE_COUNTER_AND_ADS = ads and counter.
	 * @param site
	 *            The id of the publisher site (default: DEFAULT_SITE).
	 * @param zone
	 *            The id of the zone of publisher site (default: DEFAULT_ZONE).
	 * @param ip
	 *            Deprecated.
	 *            The IP address of the carrier gateway over which the device is connecting (default: DEFAULT_IP).
	 * @param keywords
	 *            Keywords to search ad delimited by commas.
	 * @param adsType
	 *            Type of advertisement (ADS_TYPE_TEXT_ONLY - text only, ADS_TYPE_IMAGES_ONLY - image only,
	 *            ADS_TYPE_TEXT_AND_IMAGES - image and text, ADS_TYPE_SMS - SMS ad, default - DEFAULT_ADS_TYPE).<br>
	 *            SMS will be returned in XML.
	 * @param over18
	 *            Filter by ad over 18 content:<br>
	 *            OVER_18_TYPE_DENY - deny over 18 content,<br>
	 *            OVER_18_TYPE_ONLY - only over 18 content,<br>
	 *            OVER_18_TYPE_ALL - allow all ads including over 18 content). default: DEFAULT_OVER_18
	 * @param latitude
	 * @param longitude
	 * @param ua
	 *            The browser user agent of the device making the request.
	 * @param premium
	 *            Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, PREMIUM_STATUS_PREMIUM - premium only,
	 *            PREMIUM_STATUS_BOTH - both).<br>
	 *            Can be used only by premium publishers.
	 * @param test
	 *            Default setting is test mode where, if the ad code is properly installed, the ad response is "Test MODE"
	 *            (default: DEFAULT_TEST_MODE).
	 * @param count
	 *            Quantity of ads, returned by a server (Maximum: 5; Default: DEFAULT_COUNT).
	 * @param country
	 *            Country of visitor. Will override country detected by IP. (http://www.mojiva.com/docs/iso3166.csv)
	 * @param region
	 *            Region of visitor. Codes for US and Canada - http://www.mojiva.com/docs/iso3166_2.csv, others -
	 *            http://www.mojiva.com/docs/fips10_4.csv.
	 * @param textBordersEnabled
	 *            Show borders around text ads (Boolean.FALSE - non-borders, Boolean.TRUE - show borders, default:
	 *            DEFAULT_TEXT_BORDER).
	 * @param paramBorder
	 *            Borders color (#XXXXXX)
	 * @param backgroundColor
	 *            Background color (#XXXXXX)
	 * @param textColor
	 *            Link color (#XXXXXX)
	 * @param carrier
	 *            Carrier name
	 * @param imageSize
	 *            Deprecated.
	 *            Override size detection for banners (IMAGE_SIZE_SMALLEST - the smallest, IMAGE_SIZE_LARGEST - the largest).
	 * @param target
	 *            Deprecated.
	 *            Target attribute for:<br>
	 *            TARGET_BLANK - open the linked document in a new window,<br>
	 *            TARGET_SELF - open the linked document in the same frame,<br>
	 *            TARGET_PARENT - open the linked document in the parent frameset,<br>
	 *            TARGET_TOP - open the linked document in the full body of the window)<br>
	 * @param url
	 *            URL of site for which it is necessary to receive advertising.
	 * @param hashId
	 *            Unique id of Adserver instance (used for cache)
	 * @param defaultImage
	 *            Image resource name, that placed on screen when no network and cache
	 * @param internalBrowser
	 *            Open ad links in external or internal browser (default: DEFAULT_AD_BROWSER_MODE)
	 * @param updateTime
	 *            Ad reload period
	 *@deprecated This method will removed in the near future.
	 */
	public Adserver(String campaign, String appID, int mode, String site, String zone, String ip, String keywords,
			Integer adsType, Integer over18, String latitude, String longitude, String ua, Integer premium,
			Boolean test, Integer count, String country, String region, Boolean textBordersEnabled, String paramBorder,
			String backgroundColor, String textColor, String carrier, Integer imageSize, String target, String url,
			String hashId, String defaultImage, Boolean internalBrowser,
			AdClickListener clickListener, Integer updateTime, String customParameters) {
		super(mode, site, zone, keywords,
				null == latitude ? getLatitudeDetected() : latitude, null == longitude ? getLongitudeDetected() : longitude, getUADetected(),
				premium, test == Boolean.TRUE ? "1" : "0", null == country ? getCountryDetected() : country, region,
				backgroundColor, textColor, carrier, url, hashId,
				defaultImage, internalBrowser, clickListener, updateTime, customParameters);
	}

	/**
	 * @param site
	 *            The id of the publisher site (default: DEFAULT_SITE).
	 * @param zone
	 *            The id of the zone of publisher site (default: DEFAULT_ZONE).
	 */

	public Adserver (int site, int zone) {
		super (3, Integer.toString(site), Integer.toString(zone), null,
			   getLatitudeDetected(), getLongitudeDetected(), getUADetected(),
			   null, "0", getCountryDetected(), null,
			   null, null, getCarrierDetected(), null, 
			   "test", "defaultImage", Boolean.FALSE, 
			   null, null, null);
		}
	
//	public Adserver (String site, String zone, Boolean test, Integer premium, String keywords, Integer minSizeX, Integer minSizeY, Integer maxSizeX, Integer maxSizeY,
//					String backgroundColor,String textColor, Hashtable customParameters) {
//		super(3, site, zone, keywords, getLatitudeDetected(), getLongitudeDetected(), getUADetected(), premium, test == Boolean.TRUE ? "1" : "0", getCountryDetected(), null,
//				backgroundColor, textColor, getCarrierDetected(), null, "test", "defaultImage", Boolean.FALSE, null, null, null);
//	}

	/**
	 * Returns current position latitude - auto-detected
	 * 
	 * @return Current latitude
	 */
	protected static String getLatitudeDetected() {
		Coordinates coords = LocationManager.getInstance().getCoordinates();
		return null != coords ? String.valueOf(coords.getLatitude()) : null;
	}

	/**
	 * Returns current position longitude - auto-detected
	 * 
	 * @return Current longitude
	 */
	protected static String getLongitudeDetected() {
		Coordinates coords = LocationManager.getInstance().getCoordinates();
		return null != coords ? String.valueOf(coords.getLongitude()) : null;
	}

	/**
	 * Returns Composed BlackBerry User-Agent
	 * 
	 * @return User-Agent string
	 */
	protected static String getUADetected() {
		StringBuffer result = new StringBuffer(150);
		result.append("BlackBerry").append(DeviceInfo.getDeviceName()).append('/').append(DeviceInfo.getPlatformVersion());
		result.append(" Profile/").append(System.getProperty("microedition.profiles"));
		result.append(" Configuration/").append(System.getProperty("microedition.configuration"));
		result.append(" VendorID/").append(Branding.getVendorId());
		return result.toString();
	}

	/**
	 * Returns country from current locale - auto-detected
	 * 
	 * @return Current country
	 */
	protected static String getCountryDetected() {
		return Locale.getDefault().getCountry();
	}

	/**
	 * Returns current carrier name - auto-detected
	 * 
	 * @return Current carrier name
	 */
	protected static String getCarrierDetected() {
		return RadioInfo.getCurrentNetworkName();
	}

	/**
	 * Immediately update banner contents.
	 */
	public void update() {
		synchronized (runLock) {
			runLock.notify();
		}	
	}

	/**
	 * Set form click listener.
	 * @param listener
	 */
	public void setClickListener(AdClickListener listener) {
		this.clickListener = listener;
	}
}
