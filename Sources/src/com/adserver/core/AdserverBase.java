package com.adserver.core;

import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;

import org.w3c.dom.Document;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.browser.field2.BrowserFieldNavigationRequestHandler;
import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.browser.field2.BrowserFieldResourceRequestHandler;
import net.rim.device.api.browser.field2.BrowserFieldResponse;
import net.rim.device.api.browser.field2.ProtocolController;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;



import com.adserver.utils.Logger;
import com.adserver.utils.Utils;

import com.adserver.browser.*;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
// public class AdserverBase extends Observable implements RenderingApplication
// {
public class AdserverBase extends WebView {
	public static final int MODE_COUNTER_ONLY = 1;
	public static final int MODE_ADS_ONLY = 2;
	public static final int MODE_COUNTER_AND_ADS = 3;

	public static final boolean DEFAULT_AD_BROWSER_MODE = false;

	public static final String DEFAULT_IMG = "defaultimage";	 								// Internal
																									// resource
																									// for
																									// drawing
																									// resource
																									// image
	public static final int AD_RELOAD_PERIOD = 120000; 												// in milliseconds

	private final AdserverBase thisPtr = this;

	private String hashId = ""; 																	// Unique Adserver instance ID
	public String defaultImage = DEFAULT_IMG; 															// Name of default resource image
	private boolean cacheEnabled = false; 															// TODO TEST Disabled cache mode

	private String url; 																			// Main request URL
	
//	private BrowserContent browserContent = null; 													// Browser instance
	private boolean isLoaded = false;
	protected boolean adInternalBrowserEnabled; 													// Open ad links in external or
																									// internal browser (default:
																									// DEFAULT_AD_BROWSER_MODE)
	protected int adReloadPreiod = AD_RELOAD_PERIOD; 												// Ad reload timeout
	protected int adReloadPeriodSave = adReloadPreiod;
	
	private String advertiserId; 
	private String groupCode;
	private AdserverRequest request = new AdserverRequest();
	protected Object waitTillPageLoad = new Object();
	protected Object timerObject = new Object();
	
	protected int runCount = 0;
	private boolean userDefinedCoordinates = false;
	
	HttpConnection connHttp;
	//Listeners
	protected AdClickListener clickListener;
	protected EventListener eventListener;
	
	private boolean urgentUpdate = false;
	private boolean defaultImageIsSet = false;
	private boolean runWhileTrue = true;
	private String excampaigns = null;
	



	/**
	 * Constructor
	 * 
	 * @param site
	 *            The id of the publisher site (default: DEFAULT_SITE).
	 * @param zone
	 *            The id of the zone of publisher site (default: DEFAULT_ZONE).
	 * @param keywords
	 *            Keywords to search ad delimited by commas.
	 * @param latitude
	 * @param longitude
	 * @param ua
	 *            The browser user agent of the device making the request.
	 * @param premium
	 *            Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium,
	 *            PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH -
	 *            both).<br>
	 *            Can be used only by premium publishers.
	 * @param testMode
	 *            Default setting is test mode where, if the ad code is properly
	 *            installed, the ad response is "Test MODE" (default:
	 *            DEFAULT_TEST_MODE).
	 * @param country
	 *            Country of visitor. Will override country detected by IP.
	 *            (http://www.mojiva.com/docs/iso3166.csv)
	 * @param region
	 *            Region of visitor. Codes for US and Canada -
	 *            http://www.mojiva.com/docs/iso3166_2.csv, others -
	 *            http://www.mojiva.com/docs/fips10_4.csv.
	 * @param textBordersEnabled
	 *            Show borders around text ads (Boolean.FALSE - non-borders,
	 *            Boolean.TRUE - show borders, default: DEFAULT_TEXT_BORDER).
	 * @param paramBorder
	 *            Borders color (#XXXXXX)
	 * @param paramBG
	 *            Background color (#XXXXXX)
	 * @param paramLINK
	 *            Link color (#XXXXXX)
	 * @param carrier
	 *            Carrier name
	 * @param url
	 *            URL of site for which it is necessary to receive advertising.
	 * @param hashId
	 *            Unique id of Adserver instance (used for cache)
	 * @param defaultImage
	 *            Image resource name, that placed on screen when no network and
	 *            cache
	 * @param adInternalBrowserEnabled
	 *            Open ad links in external or internal browser (default:
	 *            DEFAULT_AD_BROWSER_MODE)
	 * @param clickListener
	 *            Ads click listener instance
	 * @param adReloadPreiod
	 *            Ad reload period
	 */
	public AdserverBase(int mode, String site, String zone, String keywords,
			String latitude, String longitude, String ua, Integer premium,
			String testMode, String country, String region, String paramBG,
			String paramLINK, String carrier, String url, String hashId,
			String defaultImage, Boolean adInternalBrowserEnabled,
			AdClickListener clickListener, Integer adReloadPreiod,
			String customParameters) {

//		if (null == hashId || null == defaultImage) {
//			throw new IllegalArgumentException();
//		}

		this.adInternalBrowserEnabled = adInternalBrowserEnabled.booleanValue();
		this.clickListener = clickListener;

		if (null != adReloadPreiod && adReloadPreiod.intValue() > 0) {
			this.adReloadPreiod = adReloadPreiod.intValue();
		}

			this.hashId = hashId;
			this.defaultImage = defaultImage;

			// TODO TEST Disabled cache mode
			// try {
			// createCacheWorkspace();
			// } catch (IOException e) {
			// cacheEnabled = false;
			// }

			request = new AdserverRequest();
			request.setSite(site);
			request.setZone(zone);
			request.setKeywords(keywords);
			request.setLatitude(latitude);
			request.setLongitude(longitude);
			request.setUa(ua);
			request.setPremium(premium);
			request.setTestModeEnabled(testMode.equals("1") ? Boolean.TRUE : Boolean.FALSE );
			request.setCountry(country);
			request.setRegion(region);
			request.setParamBG(paramBG);
			request.setParamLINK(paramLINK);
			request.setCarrier(carrier);
			request.setAdsType(new Integer (3));
			request.setKey(new Integer (1));
			this.url = request.createURL();
//			Logger.debug("Initial URL on AdserverRequest construction= " + url );
			
	}

//	/**
//	 * Load main ads URL
//	 */
//	private void load() {
//		cacheEnabled = false;
//		PrimaryResourceFetchThread thread;
//		HttpHeaders headers = new HttpHeaders();
//		headers.addProperty("User-Agent", getUserAgent());
//		headers.addProperty("Accept-Charset",
//				"ISO-8859-1,US-ASCII,UTF-8,UTF-16BE,Windows-1252");
//		headers.addProperty(
//				"Accept",
//				"application/vnd.rim.html, text/html, application/xhtml+xml, application/vnd.wap.xhtml+xml, text/vnd.sun.j2me.app-descriptor, " +
//				"image/vnd.rim.png, image/jpeg, application/x-vnd.rim.pme.b, application/vnd.rim.ucs, image/gif, text/plain, image/x-portable-anymap, " +
//				"image/tiff, image/x-png, image/x-portable-pixmap, image/x-icon, image/vnd.wap.wbmp, image/jpeg2000, image/x-portable-bitmap, image/bmp, " +
//				"image/x-bmp, image/x-windows-bmp, image/png, image/jp2, image/gif;anim=1, image/x-ico, image/x-portable-graymap, image/jpg, image/svg+xml, " +
//				"application/msword, application/mspowerpoint, application/vnd.ms-powerpoint, application/x-excel, application/vnd.ms-excel, application/pdf, " +
//				"application/vnd.wordperfect, application/wordperfect5.1, application/vnd.wap.wmlc;q=0.9, application/vnd.wap.wmlscriptc;q=0.7, " +
//				"text/vnd.wap.wml;q=0.7, */*;q=0.5");
//		headers.addProperty("Accept-Encoding", "gzip,deflate");
//		headers.addProperty("Accept-Language", "en-us;q=0.7,en;q=0.3");
//		// thread = new PrimaryResourceFetchThread(this.url, headers, this, true);
//		thread = new PrimaryResourceFetchThread(this.url, headers, this, true, request);
//		thread.start();
//		isLoaded = true;
//	}

//	/**
//	 * Creates a working area for the cache
//	 * 
//	 * @throws IOException
//	 */
//	// TODO TEST Disabled cache mode
//	// private void createCacheWorkspace() throws IOException {
//	// CacheManager.createDirectory(CacheManager.getInstance().getCachepath() +
//	// hashId + '/');
//	// index =
//	// CacheManager.loadCacheIndex(CacheManager.getInstance().getCachepath() +
//	// hashId + "/index");
//	// }
//
//	/**
//	 * Build browser, start loading page resources
//	 * 
//	 * @param connection
//	 *            HTTP connection
//	 * @param e
//	 * @throws RenderingException
//	 */
//	protected void processConnection(HttpConnection connection, Event e)
//			throws RenderingException {
//		if (this.connection != null) {
//			try {
//				this.connection.close();
//			} catch (IOException ignored) {
//			}
//		}
//		this.connection = connection;
////		System.out.println("Content type: " + connection.getType());
////		setParseRequired(true);
//
//		try {
//			final BrowserContent browserContent = renderingSession
//					.getBrowserContent(connection, this, e);
//			if (browserContent != null) {
//				this.browserContent = browserContent;
//				//Display page content
//				browserContent.finishLoading();
//			}
//		} catch (Exception ef) {
//			throw new RenderingException();
//		} finally {
//			// AdserverLoadedNotify notify = new AdserverLoadedNotify(this,
//			// browserContent);
//			// Application.getApplication().invokeLater(notify);
//
//			if (null != resourceThread) {
//				resourceThread.doneAddingImages();
//			}
//		}
//	}


	/**
	 * Reload page
	 */
	private void reload() {
		// AdserverReloadNotify notify = new AdserverReloadNotify();
		// synchronized (Application.getEventLock()) {
		// notify.run();
		// Application.getApplication().invokeLater(notify);
		// }
	}

//	/**
//	 * Build HTTP connection
//	 * 
//	 * @param url
//	 * @param requestHeaders
//	 * @param adserverPtr
//	 * @return
//	 * @throws IOException
//	 */
//	private HttpConnection makeConnection(String url,
//			HttpHeaders requestHeaders, final AdserverBase adserverPtr)
//			throws IOException {
//		boolean isCached = Adserver.DEFAULT_HTML.equalsIgnoreCase(url)
//				|| Adserver.DEFAULT_IMG.equalsIgnoreCase(url)
//				|| Adserver.DEFAULT_IMG_OS5.equalsIgnoreCase(url);
//		// boolean isCached = Adserver.DEFAULT_HTML.equalsIgnoreCase(url) ||
//		// Adserver.DEFAULT_IMG.equalsIgnoreCase(url) ||
//		// Adserver.DEFAULT_IMG_OS5.equalsIgnoreCase(url) || isCached(url);
//
//		// Cache disabled mode
//		if (isCached) {
//			// if (adserverPtr.isCacheEnabled() && isCached) {
//			return new AdserverStubConnection(url, requestHeaders, adserverPtr);
//		}
//		return new AdserverConnection(url, adserverPtr);
//	}


	/**
	 * Returns Composed BlackBerry User-Agent
	 * 
	 * @return User-Agent string
	 */
	protected static String getUADetected() {
//		StringBuffer result = new StringBuffer(150);
//		result.append("BlackBerry").append(DeviceInfo.getDeviceName()).append('/').append(DeviceInfo.getPlatformVersion());
//		result.append(" Profile/").append(System.getProperty("microedition.profiles"));
//		result.append(" Configuration/").append(System.getProperty("microedition.configuration"));
//		result.append(" VendorID/").append(Branding.getVendorId());
//		return result.toString();
		return System.getProperty("browser.useragent");
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	// /////////////////////////////////////////////////////////////////
	// Getters - setters

	/**
	 * Required.
	 * Set the id of the publisher site. 
	 */
	public void setSite(int site) {
		request.setSite(Integer.toString(site));
	}

	/**
	 * Get the id of the publisher site. 
	 */
	public int getSite() {
		return Integer.parseInt(request.getSite());
	}
	/**
	 * Required.
	 * Set the id of the zone of publisher site.
	 * @param zone
	 */
	public void setZone(int zone) {
		request.setZone(Integer.toString(zone));
	}
	
	/**
	 * Get the id of the zone of publisher site.
	 * @param zone
	 */
	public int getZone() {
		return Integer.parseInt(request.getZone());
	}
	
	/**
	 * Optional.
	 * Set Default setting is test mode where, if the ad code is properly installed, 
	 * the ad response is "Test MODE".
	 * @param enabled
	 */
	public void setTest(Boolean enabled) {
		request.setTestModeEnabled(enabled);
	}

	/**
	 * Optional.
	 * Get test mode setting.
	 */
	public Boolean getTest() {
		if(request != null) {
			return request.getTestModeEnabled();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, 
	 * PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). 
	 * Can be used only by premium publishers.
	 * @param premium
	 */
	public void setPremium(Integer premium) {
			request.setPremium(premium);
	}

	/**
	 * Optional.
	 * Get Filter by premium.
	 */
	public Integer getPremium() {
		if(request != null) {
			return request.getPremium();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Keywords to search ad delimited by commas.
	 * @param keywords
	 */
	public void setKeywords(String keywords) {
		if(request != null) {
			request.setKeywords(keywords);
		}
	}
	
	/**
	 * Optional.
	 * Get Keywords to search ad delimited by commas.
	 */
	public String getKeywords() {
		if(request != null) {
			return request.getKeywords();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set minimum width of advertising. 
	 * @param minSizeX
	 */
	public void setMinSizeX(Integer minSizeX) {
		if(request != null) {
			request.setMinSizeX(minSizeX);
		}
	}
	
	/**
	 * Optional.
	 * Get minimum width of advertising. 
	 */
	public Integer getMinSizeX() {
		if(request != null) {
			return request.getMinSizeX();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set minimum height of advertising. 
	 * @param minSizeY
	 */
	public void setMinSizeY(Integer minSizeY) {
		if(request != null) {
			request.setMinSizeY(minSizeY);
		}
	}
	
	/**
	 * Optional.
	 * Get minimum height of advertising. 
	 */
	public Integer getMinSizeY() {
		if(request != null) {
			return request.getMinSizeY();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set maximum width of advertising. 
	 * @param maxSizeX
	 */
	public void setMaxSizeX(Integer maxSizeX) {
		if(request != null) {
			request.setSizeX(maxSizeX);
		}
	}
	
	/**
	 * Optional.
	 * Get maximum width of advertising. 
	 */
	public Integer getMaxSizeX() {
		if(request != null) {
			return request.getSizeX();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set maximum height of advertising. 
	 * @param maxSizeY
	 */
	public void setMaxSizeY(Integer maxSizeY) {
		if(request != null) {
			request.setSizeY(maxSizeY);
		}
	}
	
	/**
	 * Optional.
	 * Get maximum height of advertising. 
	 */
	public Integer getMaxSizeY() {
		if(request != null) {
			return request.getSizeY();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Background color of advertising in HEX.
	 * @param backgroundColor
	 */
	public void setBackgroundColor(String backgroundColor) {
		if(request != null) {
			request.setParamBG(backgroundColor);
		}
	}

	/**
	 * Optional.
	 * Get Background color of advertising in HEX.
	 */
	public String getBackgroundColor() {
		if(request != null) {
			return request.getParamBG();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set Text color of links in HEX.
	 * @param textColor
	 */
	public void setTextColor(String textColor) {
		if(request != null) {
			request.setParamLINK(textColor);
		}
	}
	
	/**
	 * Optional.
	 * Get Text color of links in HEX.
	 */
	public String getTextColor() {
		if(request != null) {
			return request.getParamLINK();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Overrides the URL of ad server.
	 * @param adserverURL
	 */
	public void setAdServerUrl(String adServerUrl) {
		if(request != null) {
			request.setAdServerUrl(adServerUrl);
		}
	}

	/**
	 * Optional.
	 * Get URL of ad server.
	 */
	public String getAdServerUrl() {
		if(request != null) {
			return request.getAdServerUrl();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set user location latitude value (given in degrees.decimal degrees).
	 * @param latitude
	 */
	public void setLatitude(String latitude) {
		userDefinedCoordinates = true;
		if((request != null) && (latitude != null)) {
			request.setLatitude(latitude);
		}
	}
	
	/**
	 * Optional.
	 * Get user location latitude value (given in degrees.decimal degrees).
	 */
	public String getLatitude() {
		if(request != null) {
			String latitude = request.getLatitude();

			if(latitude != null) {
				return latitude;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set user location longitude value (given in degrees.decimal degrees).
	 * @param longitude
	 */
	public void setLongitude(String longitude) {
//		userDefinedCoordinates = true;
		if((request != null) && (longitude != null)) {
			request.setLongitude(longitude);
		}
	}
	
	/**
	 * Optional.
	 * Get user location longitude value (given in degrees.decimal degrees).
	 */
	public String getLongitude() {
		if(request != null) {
			String longitude = request.getLongitude();

			if(longitude != null) {
				return longitude;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Country of visitor. 
	 * @param country
	 */
	public void setCountry(String country) {
		if(request != null) {
			request.setCountry(country);
		}
	}
	
	/**
	 * Optional.
	 * Get Country of visitor.
	 */
	public String getCountry() {
		if(request != null) {
			return request.getCountry();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set Region of visitor. 
	 * @param region
	 */
	public void setRegion(String region) {
		if(request != null) {
			request.setRegion(region);
		}
	}
	
	/**
	 * Optional.
	 * Get Region of visitor.
	 */
	public String getRegion() {
		if(request != null) {
			return request.getRegion();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set City of the device user (with state). For US only. 
	 * @param city
	 */
	public void setCity(String city) {
		if(request != null) {
			request.setCity(city);
		}
	}

	/**
	 * Optional.
	 * Get City of the device user (with state). For US only. 
	 */
	public String getCity() {
		if(request != null) {
			return request.getCity();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Area code of a user. For US only. 
	 * @param area
	 */
	public void setArea(String area) {
		if(request != null) {
			request.setArea(area);
		}
	}

	/**
	 * Optional.
	 * Get Area code of a user. For US only. 
	 */
	public String getArea() {
		if(request != null) {
			return request.getArea();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Metro code of a user. For US only. 
	 * @param metro
	 */
	public void setMetro(String metro) {
		if(request != null) {
			request.setMetro(metro);
		}
	}
	
	/**
	 * Optional.
	 * Get Metro code of a user. For US only. 
	 */
	public String getMetro() {
		if(request != null) {
			return request.getMetro();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Zip/Postal code of user. For US only. 
	 * @param zip
	 */
	public void setZip(String zip) {
		if(request != null) {
			request.setZip(zip);
		}
	}
	
	/**
	 * Optional.
	 * Get Zip/Postal code of user. For US only. 
	 */
	public String getZip() {
		if(request != null) {
			return request.getZip();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set User carrier.
	 * @param carrier
	 */
	public void setCarrier(String carrier) {
		if(request != null) {
			request.setCarrier(carrier);
		}
	}
	
	/**
	 * Optional.
	 * Get User carrier.
	 */
	public String getCarrier() {
		if(request != null) {
			return request.getCarrier();
		} else {
			return null;
		}
	}

	
	/**
	 * Optional.
	 * Get Custom Parameters.
	 * @return Hashtable customParameters
	 */
	public Hashtable getCustomParameters() {
		if(request != null) {
			return request.getCustomParameters();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set Custom Parameters.
	 * @param customParameters
	 */
	public void setCustomParameters(Hashtable customParameters) {
		if(request != null) {
			request.setCustomParameters(customParameters);
		}
	}
	
	/**
	 * Optional.
	 * Set type of ads (1 - text only, 2 - image only, 3 - image and text, 6 - SMS ad). SMS will be ONLY returned in XML and should be used along with key=3. 
	 * @param adsType
	 */
	public void setAdsType(Integer adsType) {
		if(request != null) {
			request.setAdsType(adsType);
		}
	}
	
	/**
	 * Optional.
	 * Get Type of ads. 
	 */
	public Integer getAdsType() {
		if(request != null) {
			return request.getAdsType();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set output format. Normal format uses key = 1. Parameter key should be set to 3 in order to use XML output and to 5 in order to use JSON output. 
	 * @param adsType
	 */
	public void setKey(Integer key) {
		if(request != null) {
			request.setKey(key);
		}
	}
	
	/**
	 * Optional.
	 * Get output format. 
	 */
	public Integer getKey() {
		if(request != null) {
			return request.getKey();
		} else {
			return null;
		}
	}


	/**
	 * Optional.
	 * Set banner refresh interval (in seconds).
	 */
	public void setUpdateTime(int reloadPeriod) {

		if (reloadPeriod > 0) {
			//TODO check later
//			synchronized (waitTillPageLoad) {
//				waitTillPageLoad.notify();
//			}
			synchronized (timerObject) {
				timerObject.notify();
			}

		}
		this.adReloadPreiod = reloadPeriod * 1000;
		this.adReloadPeriodSave = reloadPeriod * 1000;
	}
	
//	/**
//	 * Set the flag which operates advertising opening.
//	 * @param internalBrowser
//	 */
//	public void setInternalBrowser (boolean internalBrowser) {
//		this.adInternalBrowserEnabled = internalBrowser;
//	}
	

	public String getDefaultImage() {
		return defaultImage;
	}

	/**
	 * Optional.
	 * Set image resource which will be shown during advertising loading if there is no advertising in a cache.
	 */
	public void setDefaultImage (String defaultImage) {
		this.defaultImage = defaultImage;
		defaultImageIsSet = true;
	}
	/**
	 * Optional.
	 * Get Advertiser id (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public String getAdvertiserId() {
		return advertiserId;
	}
	
	/**
	 * Optional.
	 * Set Advertiser id (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public void setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
		if (null != advertiserId && null != groupCode) {
			new FirstStart(advertiserId, groupCode);
		}
	}

	/**
	 * Optional.
	 * Get Group code (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public String getGroupCode() {
		return groupCode;
	}
	
	/**
	 * Optional.
	 * Set Group code (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
		if (null != advertiserId && null != groupCode) {
			new FirstStart(advertiserId, groupCode);
		}
	}
	
	/**
	 * Get interface for advertising opening.
	 */
	public AdClickListener getOnAdClickListener() {
		return clickListener;
	}

	/**
	 * Set form click listener.
	 * @param listener
	 */
	public void setClickListener(AdClickListener listener) {
		this.clickListener = listener;
	}
	
	/**
	 * Set interface for advertising opening.
	 * @param adClickListener
	 */
	public void setOnAdClickListener(AdClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public void addListener(EventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	private String  addExcampaigns (String excampaignsAddition) {
		if (null == excampaigns) excampaigns = excampaignsAddition;
		else excampaigns = excampaigns + "," + excampaignsAddition;
		return excampaigns;
	}
	
	/**
	 * Immediately update banner contents.
	 */
	public void update() {
		urgentUpdate = true;
		synchronized (timerObject) {
			timerObject.notify();
		}	
	}

	
	protected void onDisplay() {
		super.onDisplay();
		Logger.debug(" >>>>>>>>>> Form - onDisplay() - Adserver added to screen");
		
		
//		FirstStart firstStart  = new FirstStart("9417", "test");
//		firstStart.start();

		Thread refreshThread = new Thread() {

			public void run() {
				Logger.debug(" >>>>>>>>>> RefreshThread - started");
//				new FirstStart(advertiserId, groupCode);

				// check default image
				if (defaultImageIsSet) {
					displayDefaultImage();
				}
				// Loop while true
				while(runWhileTrue) {
					Logger.debug(" >>>>>>>>>> RefreshThread - begin new cycle");
					Thread resourceThread = new ResourceFetchThread();
					resourceThread.start();
						
					//waiting for resourceThread
					synchronized (waitTillPageLoad) {
						try {
							waitTillPageLoad.wait();
							Logger.debug(" >>>>>>>>>> RefreshThread - latch released");
						} catch (InterruptedException e) {
						} 
					}
					Logger.debug(" >>>>>>>>>> RefreshThread - cycle ended");
				}
				Logger.debug(" >>>>>>>>>> RefreshThread - finished");
			};
		};
		refreshThread.start();
	}
	
	protected void onUndisplay() {
		super.onUndisplay();
		//do not start refresh again
		Logger.debug(" >>>>>>>>>> Form - onUndisplay()");

		runWhileTrue = false;
		// need to edit adReloadPeriod Check loop
		adReloadPreiod = 1;
		
		synchronized (timerObject) {
			try {
				timerObject.notify();
				timerObject.notify();
			} catch (Exception e) {
			}
		}
	}
	
	protected void onVisibilityChange(boolean visible) {
		super.onVisibilityChange(visible);
		Logger.debug(" >>>>>>>>>> Form - onVisibilityChange() - visibility: " + visible);
		if (visible) {
			adReloadPreiod = adReloadPeriodSave;
			try {
				//wake up latch
				synchronized (timerObject) {
					timerObject.notify();
				}
			}catch (Exception e) {
			}
		}
		else {
			//if form is invisible - save reload period and set update time to 0
			adReloadPeriodSave = adReloadPreiod;
			adReloadPreiod = 0;
		}

		
	}
	
	private void displayDefaultImage() {
		browserField = new BrowserField();
		BrowserFieldConfig config = browserField.getConfig();

//		config.setProperty(BrowserFieldConfig.VIEWPORT_WIDTH, new Integer(Display.getWidth()));

		ProtocolController controller = new ProtocolController(browserField);
		UniversalConnectionFactory factory = new UniversalConnectionFactory();
		config.setProperty(BrowserFieldConfig.CONNECTION_FACTORY, factory);

		config.setProperty(BrowserFieldConfig.CONTROLLER, controller);
		
		controller.setNavigationRequestHandler("http", new BrowserFieldNavigationRequestHandler() {
			public void handleNavigation(BrowserFieldRequest request) throws Exception {
				Logger.debug(" >>>>>>>>>> Default image clicked!");
			}
		});
		
		controller.setResourceRequestHandler("local", new BrowserFieldResourceRequestHandler() {
			public InputConnection handleResource(BrowserFieldRequest request) throws Exception {
					try {
						try {
							String filepath = request.getURL();
							//Strip local://
							filepath = filepath.substring(8, filepath.length());
							
							InputStream is = getClass().getResourceAsStream("/" + filepath);
							return new BrowserFieldResponse(request.getURL(), is, "");
						} finally {
						}
					} catch (Exception e) {
						return null;
					}
			}
		});
		browserField.displayContent("<html><body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%\"><table height=\"100%\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"text-align:center;vertical-align:middle;\"><img src=\"local://" + defaultImage + "\"></a></td></tr></table></body></html>", "");
		Application.getApplication().invokeAndWait(new Runnable() {
			public void run() {
				add(browserField);
			}
		});
	}
	
	private class ResourceFetchThread extends Thread {
		public void run() {
			Logger.debug(" >>>>>>>>>> ResourceFetchThread - thread started");

			String requestUrl = null;
			String dataResult = null;
			boolean errorFlag = false;
			String trackUrl = null;
			urgentUpdate = false;

			if (null != eventListener) {
				Application.getApplication().invokeAndWait(new Runnable() {
					public void run() {
						eventListener.onStartLoading();
					}
				});

			}

			// compose request url with current GPS coordinates
			//Addition : check GPS every time
			if (!userDefinedCoordinates) {
				String latitude = AutoDetectParameters.getInstance().getLatitude();
				String longitude= AutoDetectParameters.getInstance().getLongitude();
				
				if ((null != latitude) || (null != longitude)) {
					request.setLatitude(latitude);
					request.setLongitude(longitude);
				}
			}
			requestUrl = request.createURL();
			System.out.println("Requested URL = " + requestUrl);
			
			try {
				dataResult = DataRequest.getResponse(requestUrl);
			} catch (Exception e) {
				//fire error callback - network error
				Logger.debug("DataRequest Error: " + e.getMessage() );
				if (null != eventListener) {
					Application.getApplication().invokeAndWait(new Runnable() {
						public void run() {
							eventListener.onError("Network error");
						}
					});
				}
			}
			
			if((null != dataResult)) {
				String externalCampaignData = Utils.scrape(dataResult, "<external_campaign", "</external_campaign>");
				//Check for error - throw callback
				if (dataResult.startsWith("<!-- invalid params -->")) {
					if (null != eventListener) {
						Application.getApplication().invokeAndWait(new Runnable() {
							public void run() {
								eventListener.onError("invalid params");
							}
						});
					}
					errorFlag = true;
				}	

				// Check for external campaign
				if (externalCampaignData.length() > 0){
					String type = Utils.scrape(externalCampaignData, "<type>", "</type>");
					String campaignId = Utils.scrape(externalCampaignData, "<campaign_id>", "</campaign_id>");
					trackUrl = Utils.scrape(externalCampaignData, "<track_url>", "</track_url>");
					String externalParams = Utils.scrape(externalCampaignData, "<external_params>", "</external_params>");
					//Parse external params
					if (type.equals("Millennial")) {
						String sdkapid = Utils.scrape(externalParams, "<param name=\"id\">", "</param>");
						String adtype = Utils.scrape(externalParams, "<param name=\"adType\">", "</param>");
						String zip = Utils.scrape(externalParams, "<param name=\"zip\">", "</param>");
						String lon = Utils.scrape(externalParams, "<param name=\"long\">", "</param>");
						String lat = Utils.scrape(externalParams, "<param name=\"lat\">", "</param>");
						
						String millenialRequestUrl = "http://ads.mp.mydas.mobi/getAd.php5?" + "sdkapid=" + sdkapid + "&auid=" + AutoDetectParameters.getInstance().getDeviceId() + "&adtype=" + adtype + "&zip=" + zip + "&long=" + lon + "&lat=" + lat;
						
						//get content second time
						try {
							dataResult = null;
							dataResult = DataRequest.getResponse(millenialRequestUrl);
						} catch (Exception e) {
						}
						if (null != dataResult) {
							if (dataResult.length() < 1) {
								//set error flag
								errorFlag = true;
								//set exCampaign
								request.setExcampaigns(addExcampaigns(campaignId));
							} 
						} else errorFlag = true;
					}
				} else  {
					//Plain HTML -  Add additional tags.
					dataResult = "<html><body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%\"><table height=\"100%\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"text-align:center;vertical-align:middle;\">" + dataResult + "</td></tr></table></body></html>";
				}
					
				//Pass content to browser
				//if ok - start new browser instance
				if (!errorFlag) {
					browserField = new BrowserField();
					BrowserFieldConfig config = browserField.getConfig();
			        config.setProperty(BrowserFieldConfig.JAVASCRIPT_ENABLED, Boolean.TRUE );
					config.setProperty(BrowserFieldConfig.ALLOW_CS_XHR, Boolean.TRUE);
					UniversalConnectionFactory factory = new UniversalConnectionFactory();
					config.setProperty(BrowserFieldConfig.CONNECTION_FACTORY, factory);
					config.setProperty(BrowserFieldConfig.CONTROLLER, new CacheProtocolController(browserField, clickListener, trackUrl));
					browserField.displayContent(dataResult, "");

					browserField.addListener(new BrowserFieldListener() {
						public void documentAborted(BrowserField browserField, Document document) throws Exception {
							//  fire error callback
							if (null != eventListener) {
								Application.getApplication().invokeAndWait(new Runnable() {
									public void run() {
										eventListener.onError("Document loading abborted");
									}
								});
							}
							super.documentAborted(browserField, document);
							releaseLatch();
						}
						
						public void documentError(BrowserField browserField, Document document) throws Exception {
							//  fire error callback
							if (null != eventListener) {
								Application.getApplication().invokeAndWait(new Runnable() {
									public void run() {
										eventListener.onError("Document loading error");
									}
								});
							}
							super.documentError(browserField, document);
							releaseLatch();
						}
						
						public void documentLoaded(BrowserField browserField, Document document) throws Exception {
							//  fire onLoaded callback
							if (null != eventListener) {
								Application.getApplication().invokeAndWait(new Runnable() {
									public void run() {
										eventListener.onLoaded();
									}
								});
							}
							super.documentLoaded(browserField, document);
							displayBrowserField();
							
							releaseLatch();
						}
					});
				} else {
					// release wait loop latch
					releaseLatch();
				}
				
			// DataResult = null -> wait and repeat
			} else {
				// release wait loop latch
				releaseLatch();
			}
			Logger.debug(" >>>>>>>>>> ResourceFetchThread - thread finished");
		}
		
		private void releaseLatch() {
			Logger.debug(" >>>>>>>>>> ResourseThread - releaseLatch() - prepare to check adReloadPreiod");
			if (!urgentUpdate) {
				synchronized (timerObject) {
					if (adReloadPreiod > 0 ) {
						try {
							Logger.debug(">>>>>>>>>> ResourseThread - releaseLatch() - waiting time in ms = " + adReloadPreiod);
							timerObject.wait(adReloadPreiod);
							Logger.debug(">>>>>>>>>> ResourseThread - releaseLatch() - waiting finished");
						} catch (InterruptedException e) {
						} 
					}
				
				}
				synchronized (timerObject) {
					if (adReloadPreiod == 0) {
						try {
							Logger.debug(">>>>>>>>>> ResourseThread - releaseLatch() - waiting time in ms = " + adReloadPreiod);
							timerObject.wait();
							Logger.debug(">>>>>>>>>> ResourseThread - releaseLatch() - waiting finished");
						} catch (InterruptedException e) {
						} 
					}
				}
			}
			synchronized (waitTillPageLoad) {
				Logger.debug(" >>>>>>>>>> ResourseThread - releaseLatch() - releasing latch");
				waitTillPageLoad.notify();
			}
		}
	}
 }
