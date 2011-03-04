package com.adserver.core;

import javax.microedition.location.Coordinates;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.CheckboxField;

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
				null == latitude ? getLatitude() : latitude, null == longitude ? getLongitude() : longitude, getUA(),
				premium, test == Boolean.TRUE ? "1" : "0", null == country ? getCountry() : country, region,
				backgroundColor, textColor, carrier, url, hashId,
				defaultImage, internalBrowser, clickListener, updateTime, customParameters);
	}

	/**
	 * @param site
	 *            The id of the publisher site (default: DEFAULT_SITE).
	 * @param zone
	 *            The id of the zone of publisher site (default: DEFAULT_ZONE).
	 */

	public Adserver (String site, String zone) {
		super (3, site, zone, null,
			   getLatitude(), getLongitude(), getUA(),
			   null, "0", getCountry(), null,
			   null, null, null, null, 
			   "test", "defaultImage", Boolean.FALSE, 
			   null, null, null);
		}

	/**
	 * Returns current position latitude
	 * 
	 * @return Current latitude
	 */
	private static String getLatitude() {
		Coordinates coords = LocationManager.getInstance().getCoordinates();
		return null != coords ? String.valueOf(coords.getLatitude()) : null;
	}

	/**
	 * Returns current position longitude
	 * 
	 * @return Current longitude
	 */
	private static String getLongitude() {
		Coordinates coords = LocationManager.getInstance().getCoordinates();
		return null != coords ? String.valueOf(coords.getLongitude()) : null;
	}

	/**
	 * Returns current IP address
	 * 
	 * @return IP address
	 */
	private static String getIP() {
		return NetworkAddressManager.getInstance().getIP();
	}

	/**
	 * Returns BlackBerry User-Agent
	 * 
	 * @return User-Agent string
	 */
	private static String getUA() {
		StringBuffer result = new StringBuffer(150);
		result.append("BlackBerry").append(DeviceInfo.getDeviceName()).append('/').append(DeviceInfo.getPlatformVersion());
		result.append(" Profile/").append(System.getProperty("microedition.profiles"));
		result.append(" Configuration/").append(System.getProperty("microedition.configuration"));
		result.append(" VendorID/").append(Branding.getVendorId());
		return result.toString();
	}

	/**
	 * Returns country from current locale
	 * 
	 * @return Current country
	 */
	private static String getCountry() {
		return Locale.getDefault().getCountry();
	}

	/**
	 * Returns current carrier name
	 * 
	 * @return Current carrier name
	 */
	private static String getCarrier() {
		return RadioInfo.getCurrentNetworkName();
	}

	public void setSite(String site) {
		AdserverRequest.site = AdserverRequest.checkStringValue(site, null);
	}
	public void setZone(String zone) {
		AdserverRequest.zone = AdserverRequest.checkStringValue(zone, null);;
	}
	public void setUa(String ua) {
		AdserverRequest.ua = AdserverRequest.checkStringValue(ua, null);
	}
	public void setTest(String testMode) {
		AdserverRequest.testMode = AdserverRequest.checkStringValue(testMode, null);
	}
	public void setPremium(Integer premium) {
		AdserverRequest.premium = AdserverRequest.checkIntegerValue(premium, null);
	}
	public void setKeywords(String keywords) {
		AdserverRequest.keywords = AdserverRequest.checkStringValue(keywords, null);
	}
	public void setMinSizeX(Integer minSizeX) {
		AdserverRequest.minSizeX = AdserverRequest.checkIntegerValue(minSizeX, null);
	}
	public void setMinSizeY(Integer minSizeY) {
		AdserverRequest.minSizeY = AdserverRequest.checkIntegerValue(minSizeY, null);
	}
	public void setMaxSizeX(Integer maxSizeX) {
		AdserverRequest.maxSizeX = AdserverRequest.checkIntegerValue(maxSizeX, null);
	}
	public void setMaxSizeY(Integer maxSizeY) {
		AdserverRequest.maxSizeY = AdserverRequest.checkIntegerValue(maxSizeY, null);
	}
	public void setParamBG(String paramBG) {
		AdserverRequest.paramBG = AdserverRequest.checkStringValue(paramBG, null);
	}
	public void setParamLINK(String paramLINK) {
		AdserverRequest.paramLINK = AdserverRequest.checkStringValue(paramLINK, null);
	}
	public void setCustomParameters(String customParameters) {
		AdserverRequest.customParameters = AdserverRequest.checkStringValue(customParameters, null);
	}
	public void setAdServerUrl(String adServerUrl) {
		AdserverRequest.adServerUrl = AdserverRequest.checkStringValue(adServerUrl, null);
	}
	public void setLatitude(String latitude) {
		AdserverRequest.latitude = AdserverRequest.checkStringValue(latitude, null);
	}
	public void setLongitude(String longitude) {
		AdserverRequest.longitude = AdserverRequest.checkStringValue(longitude, null);
	}
	public void setCountry(String country) {
		AdserverRequest.country = AdserverRequest.checkStringValue(country, null);
	}
	public void setRegion(String region) {
		AdserverRequest.region = AdserverRequest.checkStringValue(region, null);
	}
	public void setCity(String city) {
		AdserverRequest.city = AdserverRequest.checkStringValue(city, null);
	}
	public void setArea(String area) {
		AdserverRequest.area = AdserverRequest.checkStringValue(area, null);
	}
	public void setMetro(String metro) {
		AdserverRequest.metro = AdserverRequest.checkStringValue(metro, null);
	}
	public void setZip(String zip) {
		AdserverRequest.zip = AdserverRequest.checkStringValue(zip, null);
	}
	public void setCarrier(String carrier) {
		AdserverRequest.carrier = AdserverRequest.checkStringValue(carrier, null);
	}
	public void setUpdateTime(int reloadPeriod) {
		this.adReloadPreiod = reloadPeriod;
	}
	public void update() {
		if (null != Adserver.pauseLock) {
			synchronized (Adserver.pauseLock) {
				Adserver.pauseLock.notify();
			}	
		}
	}

	public void setInternalBrowser (boolean internalBrowser) {
		this.adInternalBrowserEnabled = internalBrowser;
	}
	
	public void setDefaultImage (String defaultImage) {
		this.defaultImage = defaultImage;
	}
 
	public void setClickListener(AdClickListener listener) {
		this.clickListener = listener;
	}
}
