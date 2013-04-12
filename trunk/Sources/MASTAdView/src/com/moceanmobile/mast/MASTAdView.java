package com.moceanmobile.mast;

import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.global.Formatter;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.browser.field2.ProtocolController;
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Renders text, image and HTML ads.
 * 
 * For more information visit http://developer.moceanmobile.com/SDKs.
 */
public class MASTAdView extends net.rim.device.api.ui.Manager
{
	public static final int LOG_LEVEL_NONE = 0;
	public static final int LOG_LEVEL_ERROR = 1;
	public static final int LOG_LEVEL_DEBUG = 2;
	
	// User-Agent to use for ALL HTTP/S requests
	private final String userAgent;
	
	// Fixed width/height
	private int width = 0;
	private int height = 0;
	
	// Ad request configuration
	private int site = 0;
	private int zone = 0;
	private boolean test = false;
	private boolean useInternalBrowser = false;
	private String adServerURL = Defaults.adServerURL;
	private String urlExtension = Defaults.urlExtension;
	private Hashtable adServerParams = new Hashtable();
	
	// Events logged at or lower than level
	private int logLevel = LOG_LEVEL_ERROR;
	
	// Indicates instance represents interstitial
	private boolean interstitial = false;

	// Currently rendered descriptor
	private AdDescriptor adDescriptor = null;
	
	// Pending ad request and update interval
	private AdRequest adRequest = null;
	private Timer updateTimer = null;
	private long updateInterval = 0;
	private boolean updatePending = false;
	
	// Used to determine if tracking should be invoked
	private boolean invokeTracking = false;
	
	// Location support
	private String LatitudeParam = "lat";
	private String LongitudeParam = "long";
	private int locationMaxAge = 0;
	private Timer locationUpdateTimer = null;
	private BlackBerryLocationProvider locationProvider = null;
		
	// Interstitial support
	private InterstitialScreen interstitialScreen = null;
	
	// Internal browser screen
	private BrowserScreen browserScreen = null;
	
	// Logging date format
	private SimpleDateFormat logDateFormat = 
			new SimpleDateFormat(SimpleDateFormat.DATE_SHORT | SimpleDateFormat.TIME_SHORT);
	
	// Containers for the ad content.
	private LabelField labelField = null;
	private BitmapField bitmapField = null;
	private BrowserField browserField = null;
	private VerticalFieldManager browserFieldManager = null;
	
	// Delegate handler.
	private MASTAdViewHandler handler = null;
	
	/**
	 * SDK Version
	 * 
	 * @return Version of the SDK.  
	 */
	public static String getVersion()
	{
		return Defaults.version;
	}
	
	/**
	 * Default constructor that creates an inline instance. 
	 */
	public MASTAdView()
	{
		this(false);
	}
	
	/**
	 * Creates an inline or interstitial instance.
	 * 
	 * If passing true then the interstitial instance should not be added to a manager.
	 * Use the interstitial methods to control showing and closing the ad content.
	 * 
	 * @see showInterstitial
	 * @param interstitial Set to true to create an interstitial instance.
	 * Otherwise creates a normal inline instance.
	 */
	public MASTAdView(boolean interstitial)
	{
		this(Field.FOCUSABLE | Field.FIELD_TOP | Field.USE_ALL_WIDTH, interstitial);
	}
	
	/**
	 * Creates an inline instance with the specified style.
	 * 
	 * @param style Field style flags.
	 */
	public MASTAdView(long style)
	{
		this(style, false);
	}
	
	/**
	 * Creates an inline or interstitial instance. 
	 * 
	 * @param style Field style flags used if inline.
	 * @param interstitial Set to true to create an interstitial instance.
	 * Otherwise creates a normal inline instance.
	 */
	public MASTAdView(long style, boolean interstitial)
	{
		super(style);
		
		this.interstitial = interstitial;
		userAgent = System.getProperty("browser.useragent");
		
		if (interstitial)
			interstitialScreen = new InterstitialScreen(new InterstitialHandler());
		
		// This will apply defaulting logic to a reasonable size.
		setAdWidth(0);
		setAdHeight(0);
	}
	
	/**
	 * Determines if instance is interstitial.
	 * 
	 * @return True if interstitial, false if inline.
	 */
	public boolean isInterstitial()
	{
		return interstitial;
	}
	
	/**
	 * Set the handler (delegate) for the instance.
	 * 
	 * Be aware that the handler methods MAY be called on non-UI threads.
	 * Use proper UIApplication invoke/invokeLater techniques to properly dispatch UI related logic.
	 * 
	 * @param handler Object that implements MASTAdViewHandler.
	 */
	public void setHandler(MASTAdViewHandler handler)
	{
		this.handler = handler;
	}
	
	/**
	 * Sets the desired width of the ad.  This is the value sent to the server and the value
	 * returned by Field.getPreferredWidth().
	 * 
	 * @param width Set to 0 to use Display.getWidth() or the desired width of the ad content.
	 */
	public void setAdWidth(int width)
	{
		if (width == 0)
			width = Display.getWidth();
		
		if (width != this.width)
			updateLayout();
		
		this.width = width;
	}
	
	/**
	 * @return Currently set ad height or 0 for the default.
	 */
	public int getAdWidth()
	{
		if (width == Display.getWidth())
			return 0;
		
		return width;
	}
	
	/**
	 * Sets the desired height of the ad.  This is the value sent to the server and the value
	 * returned by Field.getPreferredHeight().
	 * 
	 * @param width Set to 0 to use the default (Defaults.adHeight for inline or Display.getHeight()
	 * for interstitial) or the desired height.
	 */
	public void setAdHeight(int height)
	{
		if (height == 0)
		{
			if (interstitial == false)
			{
				height = Defaults.adHeight;	
			}
			else
			{
				height = Display.getHeight();
			}
		}	
		
		if (height != this.height)
			updateLayout();
		
		this.height = height;
	}
	
	/**
	 * @return Currently set ad height or 0 for the default.
	 */
	public int getAdHeight()
	{
		if (interstitial == false)
		{
			if (height == Defaults.adHeight)
				return 0;
		}
		else
		{
			if (height == Display.getHeight())
				return 0;
		}	

		return height;
	}
	
	/**
	 * Specifies the site for the ad server.
	 * 
	 * REQUIRED
	 * 
	 * @param site
	 */
	public void setSite(int site)
	{
		this.site = site;
	}

	/**
	 * @return Configured site.
	 */
	public int getSite()
	{
		return site;
	}

	/**
	 * Specifies the zone for the ad server.
	 * 
	 * REQUIRED
	 * 
	 * @param zone
	 */
	public void setZone(int zone)
	{
		this.zone = zone;
	}
	
	/**
	 * @return Configured zone.
	 */
	public int getZone()
	{
		return zone;
	}
	
	/**
	 * Instructs the ad server to return test ads for the configured site/zone.
	 * 
	 * Should never be set to true for production application releases.
	 * 
	 * @param test True to set test content, false for normal content.  Defaults to false.
	 */
	public void setTest(boolean test)
	{
		this.test = test;
	}
	
	/**
	 * @return True if set to request test ads or false for normal ads.
	 */
	public boolean isTest()
	{
		return test;
	}
	
	/**
	 * Configures the instance to use the internal browser for opening ad content.  If the internal
	 * browser is enabled the SDK will push a new screen with an embedded browser on the display
	 * stack.
	 * 
	 * @param useInternalBrowser  True to use the internal browser or false to use the system browser.
	 * Defaults to false.
	 */
	public void setUseInternalBrowser(boolean useInternalBrowser)
	{
		this.useInternalBrowser = useInternalBrowser;
	}
	
	/**
	 * @return True if the instance will use the internal browser or false if the instance will use the
	 * system browser.
	 */
	public boolean getUseInternalBrowser()
	{
		return useInternalBrowser;
	}
	
	/**
	 * Specifies the URL of the ad server.
	 * 
	 * OPTIONAL
	 * 
	 * @param adServerURL URL of the ad server.
	 */
	public void setAdServerURL(String adServerURL)
	{
		this.adServerURL = adServerURL;
	}
	
	/**
	 * @return Currently configured ad server URL.
	 */
	public String getAdServerURL()
	{
		return adServerURL;	
	}
	
	/**
	 * Specifies the URL extension "params" for the connection.  Not used for URLs sent to the
	 * system or internal browser.
	 * 
	 * See: http://www.blackberry.com/developers/docs/5.0.0api/javax/microedition/io/Connector.html
	 * 
	 * @param urlExtension The URL extension params to use.  Must be prepended wtih ";".  Defaults to 
	 * Defaults.urlExtension.
	 */
	public void setUrlExtension(String urlExtension)
	{
		if (urlExtension == null)
			urlExtension = "";
		
		this.urlExtension = urlExtension;
	}
	
	/**
	 * @return Currently configured URL extension.
	 */
	public String getUrlExtension()
	{
		return urlExtension;
	}
	
	/**
	 * Allows setting extra ad server parameters.  The SDK will set various parameters 
	 * based on configuration and other options.  The names and values will be URL encoded. 
	 * 
	 * For more information visit http://developer.moceanmobile.com/Mocean_Ad_Request_API.
	 * 
	 * @param name Name of the parameter to set.
	 * @param value Value for the set parameters.  A value of null will remove the parameter.
	 */
	public void setAdServerParameter(String name, String value)
	{
		if (value != null)
		{
			adServerParams.put(name, value);
		}
		else
		{
			adServerParams.remove(name);
		}
	}
	
	/**
	 * Allows inspecting set parameters.
	 * 
	 * @param name Name of the parameter to query.
	 * @return Value of the named parameter.  Null if parameter not set.
	 */
	public String getAdServerParameter(String name)
	{
		return (String) adServerParams.get(name);
	}
	
	/**
	 * Sets the log level.  Entries will be logged if they are at or below the current level.
	 * LOG_LEVEL_DEBUG will log everything.
	 * 
	 * @see MASTAdViewHandler onLogEvent method as this affects logging behavior.
	 * 
	 * @param logLevel Set to LOG_LEVEL_NONE, LOG_LEVEL_ERROR or LOG_LEVEL_DEBUG.
	 * Defaults to LOG_LEVEL_ERROR.
	 */
	public void setLogLevel(int logLevel)
	{
		this.logLevel = logLevel;
	}
	
	/**
	 * @return The currently configured log level.
	 */
	public int getLogLevel()
	{
		return logLevel;
	}
	
	/**
	 * Initiates an update.  Must be called to obtain ad content from the ad server.
	 * Will defer updating if the user invoked the internal browser until the internal
	 * browser is closed.  Will not automatically update on interval and will cancel 
	 * any automatic interval if set with update(int, boolean).
	 */
	public void update()
	{
		update(0, false);
	}
	
	/**
	 * Initiates an update for a given interval and can force an immediate update.
	 * Cancels any previous updateInterval with the new updateInterval.
	 * 
	 * @param updateInterval Set to 0 to perform a single update otherwise set to a desired
	 * interval in seconds for updating ad content from the ad server.
	 * @param force Set to true to force an update or false to allow the update to be
	 * deferred until after the internal browser (if configured) is closed.
	 */
	public void update(int updateInterval, boolean force)
	{	
		this.updateInterval = updateInterval * 1000;
		
		if (adRequest != null)
		{
			adRequest.cancel();
			adRequest = null;
		}
		
		if (updateTimer != null)
		{
			updateTimer.cancel();
		}
		
		if (!force && isInternalBrowserOpen())
		{
			updatePending = true;
			return;
		}

		internalUpdate();
	}
	
	private void restartUpdateTimer()
	{
		if (updateInterval < 1)
			return;

		if (updateTimer != null)
		{
			updateTimer.cancel();
			updateTimer = null;
		}

		updateTimer = new Timer();
		
		updateTimer.schedule(new TimerTask()
		{
			public void run()
			{
				if (interstitial)
				{
					if (isInterstitialOpen())
						return;
				}
				else
				{
					if (isVisible() == false)
						return;
				}
				
				if (isInternalBrowserOpen())
					return;
				
				internalUpdate();
			}
		}, updateInterval, updateInterval);
	}
	
	private void internalUpdate()
	{
		updatePending = false;
		
		Hashtable params = new Hashtable();
		
		// Set default parameters
		params.put("size_x", String.valueOf(width));
		params.put("size_y", String.valueOf(height));

		setRadioParameters(params);	
		setLocationParameters(params);
		
		// Import custom parameters
		Enumeration adServerParamsKeys = adServerParams.keys();
		while (adServerParamsKeys.hasMoreElements())
		{
			String key = (String) adServerParamsKeys.nextElement();
			String value = (String) adServerParams.get(key);
			params.put(key, value);
		}
		
		// Set fixed parameters
		params.put("ua", userAgent);
		params.put("version", getVersion());
		params.put("key", "3");
		params.put("count", "1");
		params.put("site", String.valueOf(site));
		params.put("zone", String.valueOf(zone));
		
		if (test)
			params.put("test", "1");
		
		String url = adServerURL + "?";
		
		Enumeration paramsKeys = params.keys();
		while (paramsKeys.hasMoreElements())
		{
			String key = (String) paramsKeys.nextElement();
			String value = (String) params.get(key);
			key = URLUTF8Encoder.encode(key);
			value = URLUTF8Encoder.encode(value);
			
			url += key + "=" + value + "&";
		}
		url = url.substring(0, url.length() - 1);
		
		url += urlExtension;
		
		logEvent(LOG_LEVEL_DEBUG, "Request URL:" + url, null);
		
		adRequest = new AdRequest(url, userAgent, new AdRequestHandler());
		adRequest.start();
	}

	/**
	 * Resets the instance to the default state and stops the update timer (if enabled).
	 * A call to update is required to obtain ad content after reset is invoked.
	 */
	public void reset()
	{
		updateInterval = 0;
		
		removeContent();
		
		if (updateTimer != null)
		{
			updateTimer.cancel();
			updateTimer = null;
		}
	}
	
	/**
	 * Removes the current ad content but does not reset the instance.  Updates will still
	 * occur on interval if configured to do so.  Closes the internal browser and invokes 
	 * closeInterstitial if the instance is configured as interstitial.
	 */
	public void removeContent()
	{
		this.adDescriptor = null;
		
		deleteAll();
		
		closeInternalBrowser();
		
		if (interstitial)
		{
			closeInterstitial();
		}
	}
	
	/**
	 * Displays the interstitial ad content (pushes screen).  Can only be called if constructed
	 * as an interstitial instance.  There is duration (infinate) and no allow close delay.
	 * 
	 * @throws UnsupportedOperationException If invoked on an inline instance.
	 */
	public void showInterstitial()
	{
		showInterstitial(0, 0);
	}

	/**
	 * Displays the interstitial ad content (pushes screen).  Can only be called if constructed
	 * as an interstitial instance.
	 * 
	 * @param duration The amount of time in seconds to display the interstitial before automatically
	 * closing it.  This time is cancled if the user interacts with the ad.
	 * @param delayCloseDuration The amount of time in seconds to delay allowing the user to close
	 * the interstitial.  Regardless of this interval, invoking closeInterstitial will close.
	 * 
	 * @throws UnsupportedOperationException If invoked on an inline instance.
	 */
	public void showInterstitial(int duration, int delayCloseDuration)
	{
		if (interstitial == false)
			throw new UnsupportedOperationException("Not an interstitial instance");
		
		interstitialScreen.setTimers(duration, delayCloseDuration);
		
		if (interstitialScreen.isDisplayed() == false)
			UiApplication.getUiApplication().pushScreen(interstitialScreen);
	}
	
	/**
	 * @return True if an interstitial ad is displayed or false if an interstitial is not open.
	 * Inline instances will always return false.
	 */
	public boolean isInterstitialOpen()
	{
		if (interstitial && interstitialScreen.isDisplayed()) 
			return true;
		
		return false;
	}
	
	/**
	 * Closes an open interstitial screen.  The interstitial should have been presented with
	 * showInterstitial prior to invoking close, if not this method does nothing.
	 * 
	 * @throws UnsupportedOperationException If invoked on an inline instance.
	 */
	public void closeInterstitial()
	{
		if (interstitial == false)
			throw new UnsupportedOperationException("Not an interstitial instance");
		
		if (interstitialScreen.isDisplayed())
			UiApplication.getUiApplication().popScreen(interstitialScreen);
	}
	
	private void showInternalBrowser(String url)
	{
		if ((browserScreen != null) && (browserScreen.isDisplayed()))
		{
			try
			{
				browserScreen.displayPage(url);
				return;
			}
			catch (Exception ex)
			{
				logEvent(LOG_LEVEL_DEBUG, "Unable to reuse internal browser screen.", ex);
			}
			
			closeInternalBrowser();
		}
		
		if (handler != null)
		{
			handler.onInternalBrowserOpen(MASTAdView.this);
		}
		
		browserScreen = new BrowserScreen(new BrowserHandler());
		UiApplication.getUiApplication().pushScreen(browserScreen);
		browserScreen.displayPage(url);	
	}
	
	/**
	 * @return True if the internall browser is currently open (pushed onto the display stack)
	 * or false if the internal browser is not displayed.
	 */
	public boolean isInternalBrowserOpen()
	{
		if ((browserScreen != null) && (browserScreen.isDisplayed()))
			return true;
		
		return false;
	}
	
	/** 
	 * Enables or disables location detection support with default options.  If enabled and a fix
	 * is determined the SDK will pre-set the lat and long ad server parameters.
	 * 
	 * Applications that already have logic to obtain a latitude and longitude fix should instead
	 * use setAdServerParameter with the lat and long coordinates.
	 * 
	 * @param enable True to enable location detection if possible or false to disable location detection.
	 */
	public void setLocationDetection(boolean enable)
	{
		Criteria criteria = null;
		
		if (enable)
		{
			BlackBerryCriteria bbCriteria = new BlackBerryCriteria();
			bbCriteria.setCostAllowed(false);
			criteria = bbCriteria;
		}
		
		enableLocationDetection(criteria, 5 * 60, 10 * 60);
	}
	
	/**
	 * Enables location detection with the desired configuration.
	 * 	 
	 * Applications that already have logic to obtain a latitude and longitude fix should instead
	 * use setAdServerParameter with the lat and long coordinates.
	 * 
	 * Disable location detection by invoking setLocationDetection(false).
	 * 
	 * @param criteria Criteria object for which to base location detection.
	 * @param refreshInterval Frequency in seconds to attempt to obtain a fix.
	 * @param maxAge The maximum age in seconds a fix is considered valid.
	 */
	public void enableLocationDetection(Criteria criteria, int refreshInterval, int maxAge)
	{
		if (locationUpdateTimer != null)
		{
			locationUpdateTimer.cancel();
			locationUpdateTimer = null;
		}
		
		if (locationProvider != null)
		{
			locationProvider.reset();
			locationProvider = null;
		}
		
		if (criteria == null)
			return;
		
		if (refreshInterval < 0)
			throw new IllegalArgumentException("refreshInterval");
		
		if (maxAge < 0)
			throw new IllegalArgumentException("maxAge");
		
		locationMaxAge = maxAge;
		
		try
		{
			locationProvider = (BlackBerryLocationProvider) BlackBerryLocationProvider.getInstance(criteria);
			locationProvider.reset();
			
			locationUpdateTimer = new Timer();
			locationUpdateTimer.schedule(new TimerTask()
			{
				public void run()
				{
					try
					{
						if (locationProvider != null)
							locationProvider.getLocation(-1);
					}
					catch (InterruptedException ex)
					{
						logEvent(LOG_LEVEL_DEBUG, "Location update interrupted.", ex);
						cancel();
					}
					catch (Exception ex)
					{
						logEvent(LOG_LEVEL_DEBUG, "Location failed to update.", ex);
					}
				}
			}, 0, refreshInterval * 1000);
		}
		catch (Exception ex)
		{
			locationProvider = null;
			locationUpdateTimer = null;
			
			logEvent(LOG_LEVEL_ERROR, "Error starting location detection.", ex);
		}
	}
	
	private void setLocationParameters(Hashtable params)
	{
		if (locationProvider == null)
			return;
		
		String lat = null;
		String lon = null;

		Location location = BlackBerryLocationProvider.getLastKnownLocation();
		if ((location != null) && location.isValid())
		{
			long locationAge = System.currentTimeMillis() - location.getTimestamp();
			locationAge /= 1000;
			
			if (locationAge <= locationMaxAge)
			{
				QualifiedCoordinates coords = location.getQualifiedCoordinates();
				lat = String.valueOf(coords.getLatitude());
				lon = String.valueOf(coords.getLongitude());
			}
			else
			{
				logEvent(LOG_LEVEL_DEBUG, "Location stale; locationAge:" + locationAge, null);
			}
		}

		if ((lat != null) && (lon != null))
		{
			params.put(LatitudeParam, lat);
			params.put(LongitudeParam, lon);
		}			
	}
	
	private void setRadioParameters(Hashtable params)
	{
		int networkCount = RadioInfo.getNumberOfNetworks(); 
		if (networkCount > 0)
		{
			int networkIndex = RadioInfo.getCurrentNetworkIndex();
			if (networkIndex > -1)
			{
				int mcc = RadioInfo.getMCC(networkIndex);
				int mnc = RadioInfo.getMNC(networkIndex);
				
				params.put("mcc", String.valueOf(mcc));
				params.put("mnc", String.valueOf(mnc));
			}
		}
	}

	private void closeInternalBrowser()
	{
		if ((browserScreen != null) && (browserScreen.isDisplayed()))
		{
			if (handler != null)
			{
				handler.onInternalBrowserClose(MASTAdView.this);
			}
			
			UiApplication.getUiApplication().popScreen(browserScreen);
			browserScreen = null;
			
			if (updatePending)
				update((int)(updateInterval / 1000), false);
		}
	}
	
	private void removeAdContainers()
	{
		if (interstitial && (interstitialScreen != null))
		{
			 interstitialScreen.deleteAll();
			 return;
		}
		
		deleteAll();
	}
	
	private void addAdContainer(Field container)
	{
		if (interstitial)
		{
			interstitialScreen.add(container);
			return;
		}
		
		add(container);
	}
	
	private boolean onOpenURL(String url)
	{
		if ((url == null) || (url.length() == 0))
			return false;
		
		boolean open = true;
		
		if (handler != null)
		{
			open = handler.onAdClicked(this, url);
		}
		
		if (open)
		{
			logEvent(LOG_LEVEL_DEBUG, "Open URL:" + url, null);
			
			if (useInternalBrowser)
			{
				final String browserUrl = url;
				UiApplication.getUiApplication().invokeLater(new Runnable()
				{
					public void run()
					{
						showInternalBrowser(browserUrl);
					}
				});
			}
			else
			{
				BrowserSession browserSession = Browser.getDefaultSession();
				browserSession.displayPage(url);
			}
			
			return true;
		}
		
		return false;
	}
	
	private void onAdRendered()
	{
		performAdTracking();
		
		if (handler != null)
		{
			handler.onAdReceived(this);
		}
	}
	
	/**
	 * @return The width specified with setAdWidth or the default.
	 */
	public int getPreferredWidth()
	{
		int width = super.getPreferredWidth();
		return width;
	}
	
	/**
	 * @return The height specified with setAdHeight or the default.
	 */
	public int getPreferredHeight()
	{
		int height = super.getPreferredHeight();
		return height;
	}
	
	protected void sublayout(int width, int height)
	{
		if (interstitial)
			throw new UnsupportedOperationException("Can not add intersstitial instance to manager");
		
		int layoutWidth = width;
		int layoutHeight = height;
		
		if (this.width < layoutWidth)
			layoutWidth = this.width;
		
		if (this.height < layoutHeight)
			layoutHeight = this.height;
	
		for (int i = 0, c = getFieldCount(); i < c; ++i)
		{
			Field f = getField(i);
			layoutChild(f, layoutWidth, layoutHeight);
			setPositionChild(f, 0, 0);
		}
		
		setExtent(layoutWidth, layoutHeight);
	}
	
	protected void onVisibilityChange(boolean visible)
	{
		super.onVisibilityChange(visible);

		performAdTracking();
	}
	
	protected boolean trackwheelClick(int status, int time)
	{
		if (isFocus() && (adDescriptor != null) && (getFieldWithFocus() != getBrowserField()))
		{
			String url = adDescriptor.getUrl();
			boolean handled = onOpenURL(url);
			return handled;
		}

		return false;
	}

	/**
	 * @return The field used to render text based ads.
	 */
	public LabelField getLabelField()
	{
		if (labelField == null)
		{
			labelField = new LabelField(null, Field.FOCUSABLE | Field.USE_ALL_WIDTH | DrawStyle.HCENTER);
		}
		return labelField;
	}

	/**
	 * @return The field used to render image based ads.
	 */
	public BitmapField getBitmapField()
	{
		if (bitmapField == null)
		{
			bitmapField = new BitmapField(null, Field.FOCUSABLE);
		}
		return bitmapField;
	}

	/**
	 * @return The field used to render HTML based ads.
	 */
	public BrowserField getBrowserField()
	{
		if (browserField == null)
		{
			BrowserFieldConfig browserFieldConfig = new BrowserFieldConfig();
			browserField = new BrowserField(browserFieldConfig);
			
			BrowserController controller = new BrowserController(browserField);
			browserFieldConfig.setProperty(BrowserFieldConfig.CONTROLLER, controller);
		}
		return browserField;
	}

	/**
	 * @return The manager that wraps the browser field to allow for scrolling.
	 */
	public VerticalFieldManager getBrowserFieldManager()
	{
		if (browserFieldManager == null)
		{
			browserFieldManager = new VerticalFieldManager(Manager.VERTICAL_SCROLL);
			browserFieldManager.add(getBrowserField());
		}
		return browserFieldManager;
	}
	
	private void performAdTracking()
	{
		if ((invokeTracking == false) || (adDescriptor == null))
			return;
		
		if (interstitial == false)
		{
			if (isVisible() == false)
				return;
		}
		else
		{
			if (interstitialScreen.isVisible() == false)
				return;
		}
		
		invokeTracking = false;
		
		final String track = adDescriptor.getTrack();
		
		if ((track != null) && (track.length() > 0))
		{
			logEvent(LOG_LEVEL_DEBUG, "Tracking URL:" + track, null);
			
			BackgroundQueue.getInstance().queueTask(new Runnable()
			{
				public void run()
				{
					try
					{
						String trackUrl = track;
						int followCount = Defaults.maxRedirects;
						while (followCount-- > 0)
						{
							String url = trackUrl + urlExtension;
							Connection connection = Connector.open(url);
							if (connection instanceof HttpConnection)
							{
								HttpConnection httpConnection = (HttpConnection) connection;
								httpConnection.setRequestMethod(HttpConnection.GET);
								httpConnection.setRequestProperty(HttpHeaders.HEADER_USER_AGENT, userAgent);
								httpConnection.setRequestProperty(HttpHeaders.HEADER_CONTENT_LENGTH, "0");
								
								httpConnection.getResponseCode();
								String location = httpConnection.getHeaderField(HttpHeaders.HEADER_LOCATION.toLowerCase());
								
								connection.close();
								
								if ((location != null) && (location.length() > 0))
								{
									trackUrl = location;
									continue;
								}
								
								// No more redirects.
								break;
							}
						}
					}
					catch (Exception ex)
					{
						logEvent(LOG_LEVEL_ERROR, "Exception performing ad tracking.", ex);
					}
				}
			});
		}
	}
	
	private void fetchBitmapAd(final AdDescriptor pendingAdDescriptor)
	{
		BackgroundQueue.getInstance().queueTask(new Runnable()
		{
			public void run()
			{
				Connection connection = null;
				try
				{
					String url = pendingAdDescriptor.getImg() + urlExtension;
					connection = Connector.open(url);
					if (connection instanceof HttpConnection)
					{
						HttpConnection httpConnection = (HttpConnection) connection;
						httpConnection.setRequestMethod(HttpConnection.GET);
						httpConnection.setRequestProperty(HttpHeaders.HEADER_USER_AGENT, userAgent);
						httpConnection.setRequestProperty(HttpHeaders.HEADER_CONTENT_LENGTH, "0");
						
						httpConnection.getResponseCode();
						InputStream inputStream = httpConnection.openInputStream();
						byte[] imageBuffer = IOUtilities.streamToBytes(inputStream);
						EncodedImage encodedImage = EncodedImage.createEncodedImage(imageBuffer, 0, imageBuffer.length);

						boolean scaleToFit = true;
						if (encodedImage instanceof GIFEncodedImage)
						{
							if (((GIFEncodedImage) encodedImage).getFrameCount() > 1)
							{
								scaleToFit = false;
							}
						}
						
						if (scaleToFit)
						{
							// Only scale up, not down.
							if ((encodedImage.getWidth() >= width) || (encodedImage.getHeight() >= height))
							{
								scaleToFit = false;
							}
						}

						final Object bitmapSource;
						Bitmap scaledBitmap = null;
						try
						{
							if (scaleToFit)
							{
								scaledBitmap = new Bitmap(width, height);
								Bitmap sourceBitmap = encodedImage.getBitmap();
								sourceBitmap.scaleInto(scaledBitmap, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FIT);
							}
						}
						catch (Exception ex)
						{
							scaleToFit = false;
							logEvent(LOG_LEVEL_ERROR, "Unable to scale bitmap.", ex);
						}
						finally
						{
							if (scaleToFit)
							{
								bitmapSource = scaledBitmap;
							}
							else
							{
								bitmapSource = encodedImage;	
							}
						}
						
						UiApplication.getUiApplication().invokeLater(new Runnable()
						{
							public void run()
							{
								renderBitmapAd(pendingAdDescriptor, bitmapSource);
							}
						});
					}
				}
				catch (Exception e)
				{
					logEvent(LOG_LEVEL_ERROR, "Error downloading image ad.", e);
					
					if (handler != null)
					{
						handler.onDownloadError(MASTAdView.this, null, e);
					}
				}
				finally
				{
					if (connection != null)
						try { connection.close(); } catch (Exception ex) {}
				}
			}
		});
	}
	
	private void renderBitmapAd(AdDescriptor pendingAdDescriptor, Object bitmapSource)
	{
		removeAdContainers();
		
		adDescriptor = pendingAdDescriptor;
		invokeTracking = true;
		
		if (bitmapSource instanceof EncodedImage)
		{
			getBitmapField().setImage((EncodedImage) bitmapSource);
		}
		else if (bitmapSource instanceof Bitmap)
		{
			getBitmapField().setBitmap((Bitmap) bitmapSource);
		}
		
		addAdContainer(getBitmapField());
		
		onAdRendered();
	}
	
	private void renderTextAd(AdDescriptor pendingAdDescriptor)
	{
		removeAdContainers();
		
		adDescriptor = pendingAdDescriptor;
		invokeTracking = true;
		
		getLabelField().setText(adDescriptor.getText());
		
		addAdContainer(getLabelField());
		
		onAdRendered();
	}
	
	private void renderWebAd(AdDescriptor pendingAdDescriptor, String content)
	{
		removeAdContainers();
		
		// Reusing BrowserField has issues....
		browserField = null;
		browserFieldManager = null;
		
		adDescriptor = pendingAdDescriptor;
		invokeTracking = false;
		
		try
		{
			String wrapper = Defaults.htmlFormat;
			content = Formatter.formatMessage(wrapper, new String[] { content });
		}
		catch (Exception ex)
		{
			logEvent(LOG_LEVEL_ERROR, "Unable to format HTML fragment.", ex);
		}

		getBrowserField().displayContent(content, "");
		
		addAdContainer(getBrowserFieldManager());
		
		onAdRendered();
	}
	
	/**
	 * Renders an ad descriptor.  Can be used to assist debugging.
	 * 
	 * Should not be used for production application releases.
	 * 
	 * @param pendingAdDescriptor
	 */
	public void renderAd(final AdDescriptor pendingAdDescriptor)
	{
		if (pendingAdDescriptor == null)
			return;
		
		String type = pendingAdDescriptor.getType();
		if (type == null)
			return;
		
		if (type.startsWith("image"))
		{
			fetchBitmapAd(pendingAdDescriptor);
			return;
		}
		
		if (type.startsWith("text"))
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
			{
				public void run()
				{
					renderTextAd(pendingAdDescriptor);
				}
			});
			return;
		}
		
		// Third party can be mediated or require a third party SDK.
		// Check for mediated content and if available use that as with non-thirdparty ads.
		// For others parse out the data points and pass to the delegate.
		if (type.startsWith("thirdparty"))
		{
			String url = pendingAdDescriptor.getUrl();
			if ((url != null) && (url.length() > 0))
			{
				String img = pendingAdDescriptor.getImg();
				if ((img != null) && (img.length() > 0))
				{
					fetchBitmapAd(pendingAdDescriptor);
					return;
				}
				
				String text = pendingAdDescriptor.getText();
				if ((text != null) && (text.length() > 0))
				{
					UiApplication.getUiApplication().invokeLater(new Runnable()
					{
						public void run()
						{
							renderTextAd(pendingAdDescriptor);
						}
					});
					return;
				}
			}
			else
			{
				String content = pendingAdDescriptor.getContent();
				if ((content != null) && (content.indexOf("client_side_external_campaign") > -1))
				{
					ThirdPartyAdDescriptor thirdPartyAdDescriptor = new ThirdPartyAdDescriptor();
					try
					{
						thirdPartyAdDescriptor.parse(content);
						
						if (handler != null)
						{
							Hashtable properties = thirdPartyAdDescriptor.getProperties();
							Hashtable params = thirdPartyAdDescriptor.getParams();
							handler.onThirdPartyEvent(this, properties, params);
						}
					}
					catch (Exception ex)
					{
						logEvent(LOG_LEVEL_ERROR, "Error parsing third party ad descriptor.", ex);
						
						if (handler != null)
						{
							handler.onDownloadError(MASTAdView.this, null, ex);
						}
						return;
					}
					return;
				}
			}
		}
		
		String content = pendingAdDescriptor.getContent();
		
		if ((content == null) || (content.length() == 0))
		{
			String message = "Ad descriptor missing content.";
			logEvent(LOG_LEVEL_ERROR, message, null);
			
			if (handler != null)
			{	
				handler.onDownloadError(MASTAdView.this, message, null);
			}

			return;
		}
		
		// Render everything else with the browser.
		final String finalContent = content;
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				renderWebAd(pendingAdDescriptor, finalContent);
			}
		});
	}
	
	private class AdRequestHandler implements AdRequest.AdRequestHandler
	{
		public void adRequestFailed(AdRequest adRequest, Exception exception)
		{
			restartUpdateTimer();
			
			logEvent(LOG_LEVEL_ERROR, "Ad request failure..", exception);
			
			if (handler != null)
			{
				handler.onDownloadError(MASTAdView.this, null, exception);
			}
		}

		public void adRequestCompleted(AdRequest adRequest)
		{
			String errorCode = adRequest.getAdResponse().getErrorCode();
			Vector adDescriptors = adRequest.getAdResponse().getAdDescriptors();
			
			if (errorCode != null)
			{
				String errorMessage = adRequest.getAdResponse().getErrorMessage();
				String message = "Ad server error.  Code:" + errorCode + " Message:" + errorMessage;;

				int logType = LOG_LEVEL_ERROR;
				if ("404".equals(errorCode))
					logType = LOG_LEVEL_DEBUG;
				
				logEvent(logType, message, null);
				
				if (handler != null)
				{
					handler.onDownloadError(MASTAdView.this, message, null);
				}
			}
			else if ((adDescriptors == null) || (adDescriptors.size() == 0))
			{
				String message = "Ad response contains no ad descriptors";
				
				logEvent(LOG_LEVEL_DEBUG, message, null);
				
				if (handler != null)
				{	
					handler.onDownloadError(MASTAdView.this, message, null);
				}
			}
			else
			{
				AdDescriptor pendingAdDescriptor = (AdDescriptor) adDescriptors.elementAt(0);
				renderAd(pendingAdDescriptor);
			}
			
			restartUpdateTimer();
		}
	}
	
	private void logEvent(int level, String message, Exception exception)
	{
		if (level < logLevel)
			return;
		
		if (handler != null)
		{
			if (handler.onLogEvent(this, level, message, exception) == false)
				return;
		}
		
		String typeString = "UNKNOWN";
		switch (level)
		{
			case LOG_LEVEL_ERROR:
				typeString = "ERROR";
				break;
			
			case LOG_LEVEL_DEBUG:
				typeString = "DEBUG";
				break;
		}
		
		Date now = new Date();
		String logOutput = "MASTAdView (" + this + ") " + this.logDateFormat.formatLocal(now.getTime()) +
				" " + typeString + ":" + message;
		
		if (exception != null)
			logOutput += " Exception:" + exception.getMessage();
		
		System.out.println(logOutput);
	}
	
	private class BrowserController extends ProtocolController
	{
		public BrowserController(BrowserField browserField)
		{
			super(browserField);
		}

		public void handleNavigationRequest(BrowserFieldRequest request) throws Exception
		{
			String url = request.getURL();
			onOpenURL(url);
		}

		public InputConnection handleResourceRequest(BrowserFieldRequest request) throws Exception
		{
			return super.handleResourceRequest(request);
		}
	}
	
	private class InterstitialHandler implements InterstitialScreen.Handler
	{
		public void onInterstitialVisibile(final InterstitialScreen sender)
		{
			performAdTracking();
		}
		
		public void onInterstitialClick(final InterstitialScreen sender)
		{
			// No longer automate closing or delaying allowing close since
			// the user interacted with the interstitial.
			sender.cancelTimers();
			
			if (adDescriptor != null)
				onOpenURL(adDescriptor.getUrl());
		}

		public void onInterstitialClose(final InterstitialScreen sender)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
			{
				public void run()
				{
					if (sender != interstitialScreen)
					{
						UiApplication.getUiApplication().popScreen(sender);
						return;
					}
					
					closeInterstitial();
				}
			});
		}
	}
	
	private class BrowserHandler implements BrowserScreen.Handler
	{
		public void onBrowserScreenClose(BrowserScreen sender)
		{
			closeInternalBrowser();
		}
	}
}
