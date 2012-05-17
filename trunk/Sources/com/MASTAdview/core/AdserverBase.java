package com.MASTAdview.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

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
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
//import net.rim.device.api.ui.component.Dialog;

import com.MASTAdview.browser.*;
import com.MASTAdview.utils.Constants;
import com.MASTAdview.utils.Logger;
import com.MASTAdview.utils.Utils;


/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
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
	protected boolean adInternalBrowserEnabled; 													// Open ad links in external or
																									// internal browser (default:
																									// DEFAULT_AD_BROWSER_MODE)
	protected int adReloadPreiod = AD_RELOAD_PERIOD; 												// Ad reload timeout
	
	private String advertiserId; 
	private String groupCode;
	public MASTAdviewRequest request = new MASTAdviewRequest();
	
	protected int runCount = 0;
	private boolean userDefinedCoordinates = false;
	
	HttpConnection connHttp;
	//Listeners
	protected AdClickListener clickListener;
	protected EventListener eventListener;
	protected OnThirdPartyRequest onThirdPartyRequest;
	
	public boolean defaultImageIsSet = false;
	private String excampaigns = null;
	CacheManager cacheManager;
	AdserverState adserverState;
	String trackUrl = null;
	boolean isSimulator = false;
	private WebViewInterstitial webViewInterstitial = null;
	private WebViewInterstitialLegacy webViewInterstitialLegacy = null;
	Thread resourceFetchThread = null;
	private int iterationNumber = 0;
	private Logger logger = null;
//	private int focusedFieldIndex = 0;
	private String AD_CONTENT_TYPE = "";
	private String AD_CONTENT_TYPE_PREVIOUS = "";
	private final String AD_CONTENT_TYPE_BROWSER = "browser";
	private final String AD_CONTENT_TYPE_VIDEO = "video";
//	private Field focusedField;
	private boolean contentAlignment = false;
	private boolean autoCollapseState = true;
	private boolean showPreviousAdonError = true;
	
	
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
		
		this.adInternalBrowserEnabled = adInternalBrowserEnabled.booleanValue();
		this.clickListener = clickListener;

		if (null != adReloadPreiod && adReloadPreiod.intValue() > 0) {
			this.adReloadPreiod = adReloadPreiod.intValue();
		}

			this.hashId = hashId;
			this.defaultImage = defaultImage;

			request = new MASTAdviewRequest();
			request.setAdserver(thisPtr);
			request.setSite(site);
			request.setZone(zone);
			request.setKeywords(keywords);
			request.setLatitude(latitude);
			request.setLongitude(longitude);
			request.setPremium(premium);
			request.setTestModeEnabled(testMode.equals("1") ? Boolean.TRUE : Boolean.FALSE );
			request.setCountry(country);
			request.setRegion(region);
			request.setParamBG(paramBG);
			request.setParamLINK(paramLINK);
			request.setCarrier(carrier);
			request.setType(new Integer (-1));
			request.setKey(new Integer (1));

			adserverState = new AdserverState(this);
			
			String userAgent = getUADetected();
			request.setUa(userAgent);
			
			getLogger().debug("Adserver : Log started");
			AD_CONTENT_TYPE = null;
			//is simulator
			isSimulator = DeviceInfo.isSimulator();
	}

	/**
	 * Returns Composed BlackBerry User-Agent
	 * 
	 * @return User-Agent string
	 */
	protected String getUADetected() {
		String userAgent ="";
		try {
			userAgent = System.getProperty("browser.useragent");
			System.out.println("User Agent: " + userAgent);
			getLogger().info(" Adserver - User Agent Detected : " + userAgent);
		} catch (Exception e) {
		}
		return userAgent;
	}

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
	public void setTest(boolean enabled) {
		request.setTestModeEnabled(new Boolean(enabled));
	}

	/**
	 * Optional.
	 * Get test mode setting.
	 */
	public boolean getTest() {
		return (request.getTestModeEnabled()).booleanValue();
	}

	/**
	 * Optional.
	 * Set Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, 
	 * PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). 
	 * Can be used only by premium publishers.
	 * @param premium
	 */
	public void setPremium(int premium) {
			request.setPremium(new Integer(premium));
	}

	/**
	 * Optional.
	 * Get Filter by premium.
	 */
	public int getPremium() {
		return (request.getPremium()).intValue();
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
	public void setMinSizeX(int minSizeX) {
		if(request != null) {
			request.setMinSizeX(new Integer(minSizeX));
		}
	}
	
	/**
	 * Optional.
	 * Get minimum width of advertising. 
	 */
	public int getMinSizeX() {
		return (request.getMinSizeX()).intValue();
	}
	
	/**
	 * Optional.
	 * Set minimum height of advertising. 
	 * @param minSizeY
	 */
	public void setMinSizeY(int minSizeY) {
			request.setMinSizeY(new Integer(minSizeY));
	}
	
	/**
	 * Optional.
	 * Get minimum height of advertising. 
	 */
	public int getMinSizeY() {
		return (request.getMinSizeY()).intValue();
	}
	
	/**
	 * Optional.
	 * Set maximum width of advertising. 
	 * @param maxSizeX
	 */
	public void setMaxSizeX(int maxSizeX) {
		request.setSizeX(new Integer(maxSizeX));
	}
	
	/**
	 * Optional.
	 * Get maximum width of advertising. 
	 */
	public int getMaxSizeX() {
		return (request.getSizeX()).intValue();
	}
	
	/**
	 * Optional.
	 * Set maximum height of advertising. 
	 * @param maxSizeY
	 */
	public void setMaxSizeY(int maxSizeY) {
		request.setSizeY(new Integer(maxSizeY));
	}
	
	/**
	 * Optional.
	 * Get maximum height of advertising. 
	 */
	public int getMaxSizeY() {
		return (request.getSizeY()).intValue();
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
	 * Type of ads to be returned (1 - text, 2 - image, 4 - richmedia ad). You can set different combinations with these values. For example, 3 = 1 + 2 (text + image), 7 = 1 + 2 + 4 (text + image + richmedia) and so on. Default value is -1 that means any type of ads can be returned.
	 * @param type
	 */
	public void setType(int type) {
		request.setType(new Integer(type));
	}
	
	
	
	/**
	 * Optional.
	 * Get Type of ads. 
	 */
	public int getType() {
		return (request.getType()).intValue();
	}
	
	/**
	 * Optional.
	 * Set output format. Normal format uses key = 1. Parameter key should be set to 3 in order to use XML output and to 5 in order to use JSON output. 
	 * @param adsType
	 */
	public void setKey(int key) {
		request.setKey(new Integer(key));
	}
	
	/**
	 * Optional.
	 * Get output format. 
	 */
	public int getKey() {
		return (request.getKey()).intValue();
	}


	/**
	 * Optional.
	 * Set banner refresh interval (in seconds).
	 */
	public void setUpdateTime(int reloadPeriod) {

		if (reloadPeriod > 0) {
			adserverState.timerNotify();
		}
		this.adReloadPreiod = reloadPeriod * 1000;
//		this.adReloadPeriodSave = reloadPeriod * 1000;
	}

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
			new FirstStart(advertiserId, groupCode, thisPtr);
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
			new FirstStart(advertiserId, groupCode, thisPtr);
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
	
	public void setThirdPartyListener(OnThirdPartyRequest onThirdPartyRequest) {
		this.onThirdPartyRequest = onThirdPartyRequest;
	}
	
	String  addExcampaigns (String excampaignsAddition) {
		if (null == excampaigns) excampaigns = excampaignsAddition;
		else excampaigns = excampaigns + "," + excampaignsAddition;
		return excampaigns;
	}
	
	/**
	 * Immediately update banner contents.
	 */
	public void update() {
		getLogger().info("Adserver : update()");
		adserverState.setUpdate(true);
		adserverState.timerNotify();
	}

	
	protected void onDisplay() {
		super.onDisplay();
		
		//is simulator
		//TODO - invoke when proper api will be available
		//		if (isSimulator) invokeIsSimulator();
		getLogger().info(" Adserver - onDisplay() - Adserver object added to screen");
		adserverState.doIt();
	}
	
	protected void onUndisplay() {
		super.onUndisplay();
		getLogger().info(" Adserver - onUndisplay() - Adserver object removed from screen");
		// need to edit adReloadPeriod Check loop
		adReloadPreiod = 1;
		adserverState.setAdserverAlive(false);
		adserverState.timerNotify();
		adserverState.worker.quit();
		try{
			deleteAll();
			browserField = null;
		}catch (Exception e) {
		}
		System.gc();
	}
	
	protected void onVisibilityChange(boolean visible) {
		super.onVisibilityChange(visible);
		getLogger().info(" Adserver - onVisibilityChange() - visibility: " + visible);
		if (visible) {
			adserverState.setVisible(true);
		}
		else {
			adserverState.setVisible(false);
		}
	}
	
	public void displayDefaultImage() {
		getLogger().info(" Adserver - displayDefaultImage()");

		BrowserFieldConfig config = new BrowserFieldConfig();
		config.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_NONE);
		config.setProperty(BrowserFieldConfig.JAVASCRIPT_ENABLED, Boolean.TRUE );
		config.setProperty(BrowserFieldConfig.ALLOW_CS_XHR, Boolean.TRUE);

		browserField = new BrowserField(config);
		config = browserField.getConfig();
		ProtocolController controller = new ProtocolController(browserField);
		UniversalConnectionFactory factory = new UniversalConnectionFactory();
		config.setProperty(BrowserFieldConfig.CONNECTION_FACTORY, factory);
		config.setProperty(BrowserFieldConfig.CONTROLLER, controller);
		
		controller.setNavigationRequestHandler("http", new BrowserFieldNavigationRequestHandler() {
			public void handleNavigation(BrowserFieldRequest request) throws Exception {
				getLogger().info(" Adserver - displayDefaultImage() - controller.setNavigationRequestHandler - Default image clicked!");
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
				getLogger().info(" Adserver - default image added to screen");
			}
		});
	}
	
	public void fetchResource() {
					String requestUrl = null;
					String dataResult = null;
					trackUrl = null;
					adserverState.setUpdate(false);
					adserverState.setSkipBrowserPhase(false);
//					AD_CONTENT_TYPE_PREVIOUS = AD_CONTENT_TYPE;

					//fire event callback onStartLoading()
					invokeOnStartLoadingCallback();

				if (!userDefinedCoordinates) {
						String latitude = AutoDetectParameters.getInstance().getLatitude();
						String longitude= AutoDetectParameters.getInstance().getLongitude();

						if ((null != latitude) || (null != longitude)) {
							getLogger().info(" Adserver : coordinates detected: latitude: "+ latitude +", longitude : " + longitude);
							request.setLatitude(latitude);
							request.setLongitude(longitude);
						}
				}

					requestUrl = request.createURL();
					getLogger().debug(" Adserver : requested URL :"+ requestUrl);
					
					//get data in main thread
					/////////////////////////////////////////////////////////////////////////
					try {
						dataResult = DataRequest.getResponse(requestUrl);
						getLogger().debug(" Adserver - RESPONSE DATA :"+ dataResult);
					} catch (Exception e) {
						//fire error callback - network error
						getLogger().info(" Adserver - DataRequest Error: " + e.getMessage() );
						invokeErrorCallback("network error");
					}

					//////////////////////////////////////////////////////////////////////////
					//parse response
					//error check
					
					
					if((null != dataResult)) {
						System.out.println("Response data size : " + dataResult.length());
						if (dataResult.indexOf("<") == -1) {
							adserverState.setSkipBrowserPhase(true);
						}

						if ((dataResult.startsWith("<!-- invalid params -->")) || (dataResult.equals(""))) {
							if ((dataResult.startsWith("<!-- invalid params -->"))) {
								getLogger().debug(" Adserver - invalid params error");
								invokeErrorCallback("invalid params");
							}
							if (null != webViewInterstitial) {
								//empty content - close screen
								webViewInterstitial.onEmptyContent();
							}
							if (null != webViewInterstitialLegacy) {
								//empty content - close screen
								webViewInterstitialLegacy.onEmptyContent();
							}
							adserverState.setSkipBrowserPhase(true);
						}
						////////////////////////////////////////////////////////////
						String videoData = Utils.scrape(dataResult, "<video", "/>");
						String externalCampaignData = Utils.scrape(dataResult, "<external_campaign", "</external_campaign>");

						//response contains video data ?
						if((videoData != null) && (videoData.length() > 0)) {
							getLogger().info(" Adserver - video data detected");
							parseVideoResponse(videoData, dataResult);
							adserverState.setSkipBrowserPhase(true);
							AD_CONTENT_TYPE = AD_CONTENT_TYPE_VIDEO;
						} 
						// response contains Third party data ?
						else if ((externalCampaignData != null) &&(externalCampaignData.length() > 0)){
							getLogger().info(" Adserver - thisr party date detected");
							dataResult = parseThirdPartyResponse(externalCampaignData, dataResult);
							AD_CONTENT_TYPE = AD_CONTENT_TYPE_BROWSER;
						} 
						//response - plain data
						else {
							//add table overlay
							if (contentAlignment) 
								dataResult = "<html><body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%\"><table height=\"100%\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"text-align:center;vertical-align:middle;\">" + dataResult + "</td></tr></table></body></html>";
							AD_CONTENT_TYPE = AD_CONTENT_TYPE_BROWSER;
						}

						//Pass content to browser
						if (!adserverState.isSkipBrowserPhase()) {
							getLogger().debug(" Adserver - send display data to browser control, isEDT:  " + Application.isEventDispatchThread());
							
							System.out.println("AD_CONTENT_TYPE = " +  AD_CONTENT_TYPE);
							System.out.println("AD_CONTENT_TYPE_PREVIOUS = " +  AD_CONTENT_TYPE_PREVIOUS);
//							printContentType();
							if ((AD_CONTENT_TYPE.equals(AD_CONTENT_TYPE_BROWSER)) && (AD_CONTENT_TYPE_PREVIOUS.equals(AD_CONTENT_TYPE_BROWSER))) {
								//JUST update
								System.out.println("Update type 1");
								updateBrowser(dataResult);
								AD_CONTENT_TYPE_PREVIOUS = AD_CONTENT_TYPE_BROWSER;
								
//								updateBrowser("<img src=\"http://192.168.1.162/new_mcn/data/content/zone/simple/1/1.png\" border=\"0\"/>");

							} else {
								System.out.println("Update type 2");
								placeBrowser();
								updateBrowser(dataResult);
								AD_CONTENT_TYPE_PREVIOUS = AD_CONTENT_TYPE_BROWSER;
								
//								updateBrowser("<img src=\"http://192.168.1.162/new_mcn/data/content/zone/simple/1/1.png\" border=\"0\"/>");
							}
						} 
						//skip browser phase
						else {
							//Errors = handle auto collapse / show previous ad logic
							if (!showPreviousAdonError) {
								if (autoCollapseState) {
									autoCollapse = true;
									if (null != browserField) browserField.displayContent("", "");
								}
							}
							// release wait loop latch
							adserverState.releaseLatch();
						}
					} 
					//dataResult = null
					else {
						if (!showPreviousAdonError) {
							if (autoCollapseState) {
								autoCollapse = true;
								if (null != browserField) browserField.displayContent("", "");
							}
						}
						adserverState.releaseLatch();
					}
	}
	public void updateBrowser (String dataResult) {
		try {
			//TODO : test required
//			focusedField = UiApplication.getUiApplication().getActiveScreen().getLeafFieldWithFocus();
			System.out.println(" ------------ > Auto collapse = " + autoCollapse);
			if (autoCollapse) {
				autoCollapse = false;
				setSize(Constants.size_x, Constants.size_y);
			} 
				
//			setSize(Constants.size_x, Constants.size_y);
			browserField.displayContent(dataResult, "");
			
		} catch (Exception e) {
			System.out.println("Failure data result: " + dataResult);
			System.out.println("Exception: " + e);
			System.out.println("Exception: " + e.getMessage());
			System.out.println("Exception: " + e.toString());
			adserverState.releaseLatch();
		}
	}
	
	private void placeBrowser() {
		//delete old
		setupBrowser(trackUrl);
		
		synchronized (UiApplication.getEventLock()) {
			try{
				deleteAll();
				add(browserField);
			}catch (Exception e) {
			}
		}
	}
	
	public void setupBrowser (String trackUrl) {
		BrowserFieldConfig config = new BrowserFieldConfig();
//		config.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_NONE);
		config.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_NONE);
		config.setProperty(BrowserFieldConfig.JAVASCRIPT_ENABLED, Boolean.TRUE );
//		config.setProperty(BrowserFieldConfig.ALLOW_CS_XHR, Boolean.TRUE);

		UniversalConnectionFactory factory = new UniversalConnectionFactory();
		config.setProperty(BrowserFieldConfig.CONNECTION_FACTORY, factory);
		config.setProperty(BrowserFieldConfig.CONTROLLER, new CacheProtocolController(browserField, clickListener, trackUrl, thisPtr));
		browserField = new BrowserField(config);

		browserField.addListener(new BrowserFieldListener() {
			public void documentAborted(BrowserField browserField, Document document) throws Exception {
				//  fire error callback
				invokeErrorCallback("Document loading abborted");
				super.documentAborted(browserField, document);
				adserverState.releaseLatch();
			}
			
			public void documentError(BrowserField browserField, Document document) throws Exception {
				//  fire error callback
				invokeErrorCallback("Document loading error");
				super.documentError(browserField, document);
				adserverState.releaseLatch();
			}
			
			public void documentLoaded(BrowserField browserField, Document document) throws Exception {
				super.documentLoaded(browserField, document);
				//TODO : do we need this ?
				
//				if (null != focusedField) focusedField.setFocus();
//				focusedField = null;

//				synchronized (UiApplication.getEventLock()) {
////					//save global focus
//					Field focusedField = UiApplication.getUiApplication().getActiveScreen().getLeafFieldWithFocus();
//					//set focus back to browser
//					AdserverBase.this.browserField.setFocus();
//					//try to restore
//					if (null != focusedField && !(focusedField instanceof BrowserField)) {
//						focusedField.setFocus();
//					}
//				}
				//  fire onLoaded callback
				invokeOnLoadedCallback();
				adserverState.releaseLatch();
			}

		});
	}

	private void parseVideoResponse(String videoData, String dataResult) {
		String videoUrl = Utils.scrape(videoData, "src=\"", "\"");
		String clickUrl = Utils.scrape(dataResult, "href=\"", "\"");
		
		//check link validity
		String urlLowercase = videoUrl.toLowerCase();
		if (urlLowercase.startsWith("http")) {
			//setup video playback
			String videoFile = "";
			if (cacheManager == null ) cacheManager = new CacheManagerImpl();
			if (cacheManager.hasCache(videoUrl)) {
				//get video fileName from cache
				videoFile =  cacheManager.getCachepath() + Utils.getMD5Hash(videoUrl);
			} else {
				//write to cache
				try {
					HttpConnection videoConnection = DataRequest.openHttpConnection(videoUrl);
					videoFile = cacheManager.createCacheWithoutMetadata(videoUrl, videoConnection);
					try {
						videoConnection.close();
					} catch (Exception e) {
					}
				} catch (IOException e) {
				}
			}
			if (videoFile.length() > 0) {
				displayVideoField(videoFile, myWidth, myHeight, clickListener, clickUrl, thisPtr);
			} else{
				//Nothing to display
				adserverState.setSkipBrowserPhase(true);
			}
		} else {
			adserverState.setSkipBrowserPhase(true);
		}
	}
	
	private String parseThirdPartyResponse(String externalCampaignData, String dataResult) {
		String result = null;
		String type = Utils.scrape(externalCampaignData, "<type>", "</type>");
		String campaignId = Utils.scrape(externalCampaignData, "<campaign_id>", "</campaign_id>");
		trackUrl = Utils.scrape(externalCampaignData, "<track_url>", "</track_url>");
		String externalParams = Utils.scrape(externalCampaignData, "<external_params>", "</external_params>");
		
		try {
			Hashtable params = new Hashtable();
			params.put("type", type);
			params.put("campaignId", campaignId);
			params.put("trackUrl", trackUrl);
			
			String[] args = split(externalParams, "</param>");
			for(int x=0;x<args.length;x++) {
				String[] vals = split(args[x], "\">");
				String val = vals.length>1 ? vals[1] : "";
				String arg = split(vals[0], "\"")[1];
				params.put(arg, val);
			}
			invokeOnThirdPartyRequestCallback(params);
			
		} catch (Exception e) {
			getLogger().network(" Adserver - parseThirdPartyResponse error: " + e.getMessage());
		}
		
//		//Parse external params
//		if (type.equals("Millennial")) {
//			String sdkapid = Utils.scrape(externalParams, "<param name=\"id\">", "</param>");
//			String adtype = Utils.scrape(externalParams, "<param name=\"adType\">", "</param>");
//			String zip = Utils.scrape(externalParams, "<param name=\"zip\">", "</param>");
//			String lon = Utils.scrape(externalParams, "<param name=\"long\">", "</param>");
//			String lat = Utils.scrape(externalParams, "<param name=\"lat\">", "</param>");
//
//			String millenialRequestUrl = "http://ads.mp.mydas.mobi/getAd.php5?" + "sdkapid=" + sdkapid + "&auid=" + AutoDetectParameters.getInstance().getDeviceId() + "&adtype=" + adtype + "&zip=" + zip + "&long=" + lon + "&lat=" + lat;
//
//			//get content second time
//			try {
//				result = DataRequest.getResponse(millenialRequestUrl);
//				getLogger().network(" Adserver - parseThirdPartyResponse - third party response: " + result);
//			} catch (Exception e) {
//			}
//		}
//		if (null != result) {
//			if (result.length() < 1) {
//				//set exCampaign
//				getLogger().info(" Adserver - setExcampaigns:" + campaignId);
//				request.setExcampaigns(addExcampaigns(campaignId));
//				adserverState.setSkipBrowserPhase(true);
//			} else {
//				//add table overlay
//				result = "<html><body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%\"><table height=\"100%\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"text-align:center;vertical-align:middle;\">" + result + "</td></tr></table></body></html>";
//			}
//		} 
//		else 
		adserverState.setSkipBrowserPhase(true);
		return result;
	}
//	public void printContentType() {
//		iterationNumber++;
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		System.out.println("Iteration: "+ iterationNumber);
//		System.out.println("Previous state: " + AD_CONTENT_TYPE_PREVIOUS);
//		System.out.println("Current content type: " + AD_CONTENT_TYPE);
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//	}
	
	private void invokeOnStartLoadingCallback() {
		if (null != eventListener) {
			getLogger().info(" Adserver - invokeOnStartLoadingCallback()");
			Application.getApplication().invokeAndWait(new Runnable() {
				public void run() {
					eventListener.onStartLoading();
				}
			});
		}
	}

	private void invokeErrorCallback(final String errorMsg) {
		if (null != eventListener) {
			getLogger().info(" Adserver - invokeErrorCallback()");
			Application.getApplication().invokeAndWait(new Runnable() {
				public void run() {
					eventListener.onError(errorMsg);
				}
			});
		}
	}

	private void invokeOnLoadedCallback() {
		if (null != eventListener) {
			getLogger().info(" Adserver - invokeOnLoadedCallback()");
			Application.getApplication().invokeAndWait(new Runnable() {
				public void run() {
					eventListener.onLoaded();
				}
			});
		}
	}
	
	private void invokeOnThirdPartyRequestCallback(final Hashtable params) {
		if (null != onThirdPartyRequest) {
			getLogger().info(" Adserver - OnThirdPartyRequest() callback");
			Application.getApplication().invokeAndWait(new Runnable() {
				public void run() {
					onThirdPartyRequest.event(params);
				}
			});
		}
	}

	
//	TODO: proper implamentation
//	private void invokeIsSimulator() {
//		Application.getApplication().invokeAndWait(new Runnable() {
//			public void run() {
//				Dialog.alert("isSimulator()");
//			}
//		});
//	}

	public void setWebViewInterstitial(WebViewInterstitial webViewInterstitial) {
		this.webViewInterstitial = webViewInterstitial;
	}
	public void setWebViewInterstitialLegacy(WebViewInterstitialLegacy webViewInterstitialLegacy) {
		this.webViewInterstitialLegacy = webViewInterstitialLegacy;
	}
	
	public Logger getLogger() {
		if (null == logger) {
			//Log started
			int hashCode = thisPtr.getClass().hashCode();
			hashId = Integer.toString(hashCode);
			logger = new Logger(hashId);
		}
		return logger;
	}

	public void setLogLevel(int logLevel) {
		getLogger().setLogLevel(logLevel);
	}
	
	public void setLoggerId (String id) {
		getLogger().setHashId(id);
	}
	
	public void setContentAlignment(boolean enable) {
		contentAlignment = enable;
	}
	
	public void setLocationDetection(boolean enable)
	{
		AutoDetectParameters.getInstance().setLocationDetection(enable);
	}
	
	public void setLocationMinWaitMillis(int value)
	{
		AutoDetectParameters.getInstance().setLocationMinWaitMillis(value);
	}
	
	public String[] split(String strString, String strDelimiter)
	{
		int iOccurrences = 0;
		int iIndexOfInnerString = 0;
		int iIndexOfDelimiter = 0;
		int iCounter = 0;

		// Check for null input strings.
		if (strString == null)
		{
			throw new NullPointerException("Input string cannot be null.");
		}
		// Check for null or empty delimiter
		// strings.
		if (strDelimiter.length() <= 0 || strDelimiter == null)
		{
			throw new NullPointerException("Delimeter cannot be null or empty.");
		}

		// If strString begins with delimiter
		// then remove it in
		// order
		// to comply with the desired format.

		if (strString.startsWith(strDelimiter))
		{
			strString = strString.substring(strDelimiter.length());
		}

		// If strString does not end with the
		// delimiter then add it
		// to the string in order to comply with
		// the desired format.
		if (!strString.endsWith(strDelimiter))
		{
			strString += strDelimiter;
		}

		// Count occurrences of the delimiter in
		// the string.
		// Occurrences should be the same amount
		// of inner strings.
		while((iIndexOfDelimiter= strString.indexOf(strDelimiter,iIndexOfInnerString))!=-1)
		{
			iOccurrences += 1;
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
		}

		// Declare the array with the correct
		// size.
		String[] strArray = new String[iOccurrences];

		// Reset the indices.
		iIndexOfInnerString = 0;
		iIndexOfDelimiter = 0;

		// Walk across the string again and this
		// time add the
		// strings to the array.
		while((iIndexOfDelimiter= strString.indexOf(strDelimiter,iIndexOfInnerString))!=-1)
		{

			// Add string to
			// array.
			strArray[iCounter] = strString.substring(iIndexOfInnerString, iIndexOfDelimiter);

			// Increment the
			// index to the next
			// character after
			// the next
			// delimiter.
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();

			// Inc the counter.
			iCounter += 1;
		}
            return strArray;
	}
	
    public static String[] splitString(String original, String separator) {
        Vector nodes = new Vector();
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }
        }

        return result;
    }

    //25.02.2012
    
    public boolean getAutoCollapse () {
    	return autoCollapse;
    }
    
    public void setSize(int width, int height) {
		Constants.size_x = width;
		Constants.size_y = height;
		sizeSet = true;
		myWidth = width;
		myHeight = height;
		super.sublayout(width, height);
	}
    
	public void autoCollapse(boolean value) {
		autoCollapse = value;
		autoCollapseState = value;
	}
	
	public void showPreviousAdonError(boolean value) {
		showPreviousAdonError = value;
	}

 }
