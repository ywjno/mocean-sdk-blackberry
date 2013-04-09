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
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

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
	private String urlExtension = ";deviceside=true";
	private Hashtable adServerParams = new Hashtable();
	
	// Events logged at or lower than level
	private int logLevel = LOG_LEVEL_NONE;
	
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
	
	public static String getVersion()
	{
		return Defaults.version;
	}
	
	public MASTAdView()
	{
		this(false);
	}
	
	public MASTAdView(boolean interstitial)
	{
		this(Field.FOCUSABLE | Field.FIELD_TOP | Field.USE_ALL_WIDTH, interstitial);
	}
	
	public MASTAdView(long style)
	{
		this(style, false);
	}
	
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
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#isInterstitial()
	 */
	public boolean isInterstitial()
	{
		return interstitial;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setHandler(com.moceanmobile.mast.MASTAdViewHandler)
	 */
	public void setHandler(MASTAdViewHandler handler)
	{
		this.handler = handler;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setWidth(int)
	 */
	public void setAdWidth(int width)
	{
		if (width == 0)
			width = Display.getWidth();
		
		if (width != this.width)
			updateLayout();
		
		this.width = width;
	}
	
	public int getAdWidth()
	{
		if (width == Display.getWidth())
			return 0;
		
		return width;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setHeight(int)
	 */
	public void setAdHeight(int height)
	{
		if (height == 0)
		{
			if (interstitial == false)
			{
				height = 50;	
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
	
	public int getAdHeight()
	{
		if (height == Display.getHeight())
			return 0;
		
		return height;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setSite(int)
	 */
	public void setSite(int site)
	{
		this.site = site;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getSite()
	 */
	public int getSite()
	{
		return site;
	}

	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setZone(int)
	 */
	public void setZone(int zone)
	{
		this.zone = zone;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getZone()
	 */
	public int getZone()
	{
		return zone;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setTest(boolean)
	 */
	public void setTest(boolean test)
	{
		this.test = test;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#isTest()
	 */
	public boolean isTest()
	{
		return test;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setUseInternalBrowser(boolean)
	 */
	public void setUseInternalBrowser(boolean useInternalBrowser)
	{
		this.useInternalBrowser = useInternalBrowser;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getUseInternalBrowser()
	 */
	public boolean getUseInternalBrowser()
	{
		return useInternalBrowser;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setAdServerURL(java.lang.String)
	 */
	public void setAdServerURL(String adServerURL)
	{
		this.adServerURL = adServerURL;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getAdServerURL()
	 */
	public String getAdServerURL()
	{
		return adServerURL;	
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setUrlExtension(java.lang.String)
	 */
	public void setUrlExtension(String urlExtension)
	{
		if (urlExtension == null)
			urlExtension = "";
		
		this.urlExtension = urlExtension;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getUrlExtension()
	 */
	public String getUrlExtension()
	{
		return urlExtension;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setAdServerParameter(java.lang.String, java.lang.String)
	 */
	public void setAdServerParameter(String name, String value)
	{
		adServerParams.put(name, value);
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getAdServerParameter(java.lang.String)
	 */
	public String getAdServerParameter(String name)
	{
		return (String) adServerParams.get(name);
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setLogLevel(int)
	 */
	public void setLogLevel(int logLevel)
	{
		this.logLevel = logLevel;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getLogLevel()
	 */
	public int getLogLevel()
	{
		return logLevel;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#update()
	 */
	public void update()
	{
		update(0, false);
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#update(int, boolean)
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
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#reset()
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
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#removeContent()
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
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#showInterstitial()
	 */
	public void showInterstitial()
	{
		showInterstitial(0, 0);
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#showInterstitial(int, int)
	 */
	public void showInterstitial(int duration, int delayCloseDuration)
	{
		if (interstitial == false)
			throw new UnsupportedOperationException("Not an interstitial instance");
		
		interstitialScreen.setTimers(duration, delayCloseDuration);
		
		if (interstitialScreen.isDisplayed() == false)
			UiApplication.getUiApplication().pushScreen(interstitialScreen);
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#isInterstitialOpen()
	 */
	public boolean isInterstitialOpen()
	{
		if (interstitial && interstitialScreen.isDisplayed()) 
			return true;
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#closeInterstitial()
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
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#isInternalBrowserOpen()
	 */
	public boolean isInternalBrowserOpen()
	{
		if ((browserScreen != null) && (browserScreen.isDisplayed()))
			return true;
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#setLocationDetection(boolean)
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
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#enableLocationDetection(javax.microedition.location.Criteria, int, int)
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
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getPreferredWidth()
	 */
	public int getPreferredWidth()
	{
		int width = super.getPreferredWidth();
		return width;
	}
	
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getPreferredHeight()
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

	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getLabelField()
	 */
	public LabelField getLabelField()
	{
		if (labelField == null)
		{
			labelField = new LabelField(null, Field.FOCUSABLE | Field.USE_ALL_WIDTH | DrawStyle.HCENTER);
		}
		return labelField;
	}

	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getBitmapField()
	 */
	public BitmapField getBitmapField()
	{
		if (bitmapField == null)
		{
			bitmapField = new BitmapField(null, Field.FOCUSABLE);
		}
		return bitmapField;
	}

	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getBrowserField()
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

	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#getBrowserFieldManager()
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
	
	// Can be called from main thread or processing thread.
	/* (non-Javadoc)
	 * @see com.moceanmobile.mast.MASTAdView#renderAd(com.moceanmobile.mast.AdDescriptor)
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
