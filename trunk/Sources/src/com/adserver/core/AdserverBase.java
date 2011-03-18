package com.adserver.core;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.HttpConnection;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RedirectEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.Status;

import com.adserver.utils.EventListener;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
// public class AdserverBase extends Observable implements RenderingApplication
// {
public class AdserverBase extends WebView implements RenderingApplication {
	public static final int MODE_COUNTER_ONLY = 1;
	public static final int MODE_ADS_ONLY = 2;
	public static final int MODE_COUNTER_AND_ADS = 3;

	public static final boolean DEFAULT_AD_BROWSER_MODE = false;

	private static final String REFERER = "referer"; 												// Default referer for
																									// browser
	public static final String DEFAULT_HTML = "default.html"; 										// Internal
																									// resource for
																									// drawing
																									// resource
																									// image
	public static final String DEFAULT_HOST = "test.ru"; 											// Default host for
																									// default stub
	public static final String DEFAULT_IMG = "http://defaultImage/"; 								// Internal
																									// resource
																									// for
																									// drawing
																									// resource
																									// image
	public static final String DEFAULT_IMG_OS5 = "http://" + DEFAULT_HOST + "/defaultImage"; 		// Internal resource for drawing resource image

	public static final int AD_RELOAD_PERIOD = 12000; 												// in milliseconds

	private final AdserverBase thisPtr = this;

	private RenderingSession renderingSession; 														// Used for configurating page
																									// rendering
	private HttpConnection connection = null;
	
	// private WebView webView = new WebView(); 													// Ads view instance

	private SecondaryResourceFetchThread resourceThread; 											// Internal thread for
																									// loading page
																									// resources
	private String hashId = ""; 																	// Unique Adserver instance ID
	protected String defaultImage = ""; 															// Name of default resource image

	// TODO TEST Disabled cache mode
	// private Hashtable index = new Hashtable(0); 													// Cached links storage:
																									// (link;link hash). Link hash used for naming cached files
	private boolean cacheEnabled = false; 															// TODO TEST Disabled cache mode

	private String url; 																			// Main request URL
	
	private BrowserContent browserContent = null; 													// Browser instance
	private boolean isLoaded = false;
	protected boolean adInternalBrowserEnabled; 													// Open ad links in external or
																									// internal browser (default:
																									// DEFAULT_AD_BROWSER_MODE)
	protected AdClickListener clickListener;
	protected int adReloadPreiod = AD_RELOAD_PERIOD; 												// Ad reload timeout

	private String advertiserId; 
	private String groupCode;
	private AdserverRequest request = new AdserverRequest();
	protected Object pauseLock = new Object();
	private Object runLock = new Object();



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

		if (null == hashId || null == defaultImage) {
			throw new IllegalArgumentException();
		}

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
//PUSHME
//			request = new AdserverRequest(site, zone, keywords, latitude,
//					longitude, ua, premium, testMode, country, region, paramBG,
//					paramLINK, carrier, url, customParameters);

			// AdserverRequest request = new AdserverRequest(site, zone, ip,
			// keywords, adsType, over18, latitude, longitude, ua,
			// premium, key, testMode, count, country, region,
			// textBordersEnabled, paramBorder, paramBG, paramLINK, carrier,
			// imageSize, target, url, pixelModeEnabled);
//			this.url = request.createURL();
//			System.out.println("URL: " + this.url);

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
			this.url = request.createURL();
			System.out.println("Initial URL on request= " + url );


			renderingSession = RenderingSession.getNewInstance();
			renderingSession.getRenderingOptions().setProperty(
					RenderingOptions.CORE_OPTIONS_GUID,
					RenderingOptions.JAVASCRIPT_ENABLED, true);
			renderingSession.getRenderingOptions().setProperty(
					RenderingOptions.CORE_OPTIONS_GUID,
					RenderingOptions.SHOW_IMAGES_IN_HTML, true);
			renderingSession.getRenderingOptions().setProperty(
					RenderingOptions.CORE_OPTIONS_GUID,
					RenderingOptions.SHOW_TABLES_IN_HTML, true);
			renderingSession.getRenderingOptions().setProperty(
					RenderingOptions.CORE_OPTIONS_GUID,
					RenderingOptions.USE_BACKGROUND_IMAGES, true);

			// load();
			/*
			 * String lastURL = null;//(String) index.get("lastURL"); if (null
			 * != lastURL && lastURL.length() != 0) { PrimaryResourceFetchThread
			 * cacheThread; cacheThread = new
			 * PrimaryResourceFetchThread(lastURL, null, this);
			 * cacheThread.start(); } else { // TODO TEST Disabled cache mode //
			 * cacheEnabled = true; PrimaryResourceFetchThread cacheThread;
			 * cacheThread = new PrimaryResourceFetchThread(DEFAULT_HTML, null,
			 * this); cacheThread.start(); }
			 */
//Trying
//			PrimaryResourceFetchThread cacheThread;
//			cacheThread = new PrimaryResourceFetchThread(DEFAULT_HTML, null,
//					this);
//			cacheThread.start();

	}

	/**
	 * Load main ads URL
	 */
	private void load() {
		cacheEnabled = false;
		PrimaryResourceFetchThread thread;
		HttpHeaders headers = new HttpHeaders();
		headers.addProperty("User-Agent", getUserAgent());
		headers.addProperty("Accept-Charset",
				"ISO-8859-1,US-ASCII,UTF-8,UTF-16BE,Windows-1252");
		headers.addProperty(
				"Accept",
				"application/vnd.rim.html, text/html, application/xhtml+xml, application/vnd.wap.xhtml+xml, text/vnd.sun.j2me.app-descriptor, " +
				"image/vnd.rim.png, image/jpeg, application/x-vnd.rim.pme.b, application/vnd.rim.ucs, image/gif, text/plain, image/x-portable-anymap, " +
				"image/tiff, image/x-png, image/x-portable-pixmap, image/x-icon, image/vnd.wap.wbmp, image/jpeg2000, image/x-portable-bitmap, image/bmp, " +
				"image/x-bmp, image/x-windows-bmp, image/png, image/jp2, image/gif;anim=1, image/x-ico, image/x-portable-graymap, image/jpg, image/svg+xml, " +
				"application/msword, application/mspowerpoint, application/vnd.ms-powerpoint, application/x-excel, application/vnd.ms-excel, application/pdf, " +
				"application/vnd.wordperfect, application/wordperfect5.1, application/vnd.wap.wmlc;q=0.9, application/vnd.wap.wmlscriptc;q=0.7, " +
				"text/vnd.wap.wml;q=0.7, */*;q=0.5");
		headers.addProperty("Accept-Encoding", "gzip,deflate");
		headers.addProperty("Accept-Language", "en-us;q=0.7,en;q=0.3");
		// thread = new PrimaryResourceFetchThread(this.url, headers, this, true);
		thread = new PrimaryResourceFetchThread(this.url, headers, this, true, request);
		thread.start();
		isLoaded = true;
	}

	/**
	 * Creates a working area for the cache
	 * 
	 * @throws IOException
	 */
	// TODO TEST Disabled cache mode
	// private void createCacheWorkspace() throws IOException {
	// CacheManager.createDirectory(CacheManager.getInstance().getCachepath() +
	// hashId + '/');
	// index =
	// CacheManager.loadCacheIndex(CacheManager.getInstance().getCachepath() +
	// hashId + "/index");
	// }

	/**
	 * Build browser, start loading page resources
	 * 
	 * @param connection
	 *            HTTP connection
	 * @param e
	 * @throws RenderingException
	 */
	protected void processConnection(HttpConnection connection, Event e)
			throws RenderingException {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (IOException ignored) {
			}
		}
		this.connection = connection;

		try {
			final BrowserContent browserContent = renderingSession
					.getBrowserContent(connection, this, e);
			if (browserContent != null) {
				this.browserContent = browserContent;
				browserContent.finishLoading();
			}
		} catch (Exception ef) {
			throw new RenderingException();
		} finally {
			// AdserverLoadedNotify notify = new AdserverLoadedNotify(this,
			// browserContent);
			// Application.getApplication().invokeLater(notify);

			if (null != resourceThread) {
				resourceThread.doneAddingImages();
			}
		}
	}

	/**
	 * Browser event dispatcher
	 * 
	 * @param event
	 * @return
	 */
	public Object eventOccurred(Event event) {
		int eventId = event.getUID();
		boolean clickResult = false;

		switch (eventId) {
		case Event.EVENT_URL_REQUESTED: {
			if (null != clickListener) {
				// clickListener.didAdClicked(this);
				clickResult = clickListener.didAdClicked(((UrlRequestedEvent) event).getURL());
			}

			// if (adInternalBrowserEnabled && clickResult) {
			// cacheEnabled = false;
			// UrlRequestedEvent urlRequestedEvent = (UrlRequestedEvent) event;
			// HttpHeaders requestHeaders = new HttpHeaders();
			// PrimaryResourceFetchThread thread = new
			// PrimaryResourceFetchThread(urlRequestedEvent.getURL(),
			// requestHeaders, this);
			// thread.start();
			// } else {
			// }
			// External browser and click result - false
			if (!adInternalBrowserEnabled && !clickResult) {
				// try {
				UrlRequestedEvent urlRequestedEvent = (UrlRequestedEvent) event;
				Browser.getDefaultSession().displayPage(urlRequestedEvent.getURL());
				// } catch (IOException e) {
				// AdserverNoNetworkNotify notify = new
				// AdserverNoNetworkNotify(thisPtr);
				// Application.getApplication().invokeLater(notify);
				// }
			}

			break;
		}
		case Event.EVENT_REDIRECT: {
			RedirectEvent e = (RedirectEvent) event;
			String referrer = e.getSourceURL();

			switch (e.getType()) {
			case RedirectEvent.TYPE_SINGLE_FRAME_REDIRECT:
				Application.getApplication().invokeAndWait(new Runnable() {
					public void run() {
						Status.show("You are being redirected to a different page...");
					}
				});
				break;

			case RedirectEvent.TYPE_JAVASCRIPT:
				break;

			case RedirectEvent.TYPE_META:
				referrer = null;
				break;

			case RedirectEvent.TYPE_300_REDIRECT:
				Object eventSource = e.getSource();
				if (eventSource instanceof HttpConnection) {
					referrer = ((HttpConnection) eventSource)
							.getRequestProperty(REFERER);
				}

				break;
			}

			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setProperty(REFERER, referrer);
			PrimaryResourceFetchThread thread = new PrimaryResourceFetchThread(
					e.getLocation(), requestHeaders, this);
			thread.start();
			break;
		}

		case Event.EVENT_SET_HEADER: // No cache support.
		case Event.EVENT_SET_HTTP_COOKIE: // No cookie support.
		case Event.EVENT_HISTORY: // No history support.
		case Event.EVENT_EXECUTING_SCRIPT: // No progress bar is supported.
		case Event.EVENT_FULL_WINDOW: // No full window support.
		case Event.EVENT_STOP: // No stop loading support.
		default:
		}

		return null;
	}

	public int getAvailableHeight(BrowserContent browserField) {
		return Display.getHeight();
	}

	public int getAvailableWidth(BrowserContent browserField) {
		return Display.getWidth();
	}

	public int getHistoryPosition(BrowserContent browserField) {
		return 0;
	}

	public String getHTTPCookie(String url) {
		return null;
	}

	/**
	 * Load page resource
	 * 
	 * @param resource
	 * @param referrer
	 * @return
	 */
	public HttpConnection getResource(RequestedResource resource,
			BrowserContent referrer) {
		if (null == resource /* || resource.isCacheOnly() */
				|| null == resource.getUrl()) {
			return null;
		}

		if (referrer == null) {
			try {
				return makeConnection(resource.getUrl(),
						resource.getRequestHeaders(), this);
			} catch (IOException e) {
				return null;
			}
		} else if (null != resourceThread) {
			resourceThread.enqueue(resource, referrer);
		}

		return null;
	}

	public void invokeRunnable(Runnable runnable) {
		(new Thread(runnable)).start();
	}

	/**
	 * Page loaded dispatcher
	 */
	private class AdserverLoadedNotify implements Runnable {
		private AdserverBase application;
		private BrowserContent content;

		private AdserverLoadedNotify(AdserverBase application,
				BrowserContent content) {
			this.application = application;
			this.content = content;
		}

		public void run() {
			// webView.setContent(content);
			thisPtr.setContent(content);
			// System.out.println("Load notify!");
			application.onLoaded();

			// TODO TEST Disabled cache mode
			// if (isLoaded) {
			// try {
			// CacheManager.saveCacheIndex(CacheManager.getInstance().getCachepath()
			// + hashId + "/index", index, url);
			// } catch (IOException e) {
			// setCacheEnabled(false);
			// }
			// }
			load();
		}
	}

	/**
	 * Page start loading dispatcher
	 */
	private class AdserverStartLoadingNotify implements Runnable {
		private AdserverBase application;
		private String url;

		private AdserverStartLoadingNotify(AdserverBase application, String url) {
			this.application = application;
			this.url = url;
		}

		public void run() {
			application.onStartLoading();
		}
	}

	/**
	 * Loading error dispatcher
	 */
	public static class AdserverNoNetworkNotify implements Runnable {
		private AdserverBase application;
		private String error;

		public AdserverNoNetworkNotify(AdserverBase application, String error) {
			this.application = application;
			this.error = error;
		}

		public void run() {
			application.onError(error);
		}
	}

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

	/**
	 * Build HTTP connection
	 * 
	 * @param url
	 * @param requestHeaders
	 * @param adserverPtr
	 * @return
	 * @throws IOException
	 */
	private HttpConnection makeConnection(String url,
			HttpHeaders requestHeaders, final AdserverBase adserverPtr)
			throws IOException {
		boolean isCached = Adserver.DEFAULT_HTML.equalsIgnoreCase(url)
				|| Adserver.DEFAULT_IMG.equalsIgnoreCase(url)
				|| Adserver.DEFAULT_IMG_OS5.equalsIgnoreCase(url);
		// boolean isCached = Adserver.DEFAULT_HTML.equalsIgnoreCase(url) ||
		// Adserver.DEFAULT_IMG.equalsIgnoreCase(url) ||
		// Adserver.DEFAULT_IMG_OS5.equalsIgnoreCase(url) || isCached(url);

		// Cache disabled mode
		if (isCached) {
			// if (adserverPtr.isCacheEnabled() && isCached) {
			return new AdserverStubConnection(url, requestHeaders, adserverPtr);
		}
		return new AdserverConnection(url, adserverPtr);
	}

	/**
	 * Page loading thread
	 */
	private class PrimaryResourceFetchThread extends Thread {
		private AdserverBase application;
		private AdserverRequest request;
		private HttpHeaders requestHeaders;
		private String url;
		private boolean sleepEnabled = false;

		PrimaryResourceFetchThread(String url, HttpHeaders requestHeaders,
				AdserverBase application) {
			this(url, requestHeaders, application, false, null);
		}

		PrimaryResourceFetchThread(String url, HttpHeaders requestHeaders,
				AdserverBase application, boolean sleepEnabled,
				AdserverRequest request) {
			this.url = url;
			this.request = request;
			this.requestHeaders = requestHeaders;
			this.application = application;
			this.sleepEnabled = sleepEnabled;
		}

		public void run() {
			synchronized (runLock) {
				if (adReloadPreiod == 0) {
					try {
						runLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			synchronized (pauseLock) {
				if (sleepEnabled) {
					try {
						pauseLock.wait(adReloadPreiod);
					} catch (InterruptedException ignored) {
					}
				}
			}
			resourceThread = new SecondaryResourceFetchThread(application);
			HttpConnection connection;
			try {
				if (null != request) {
					this.url = request.createURL();
					System.out.println("URL = " + this.url);
				}
				connection = makeConnection(url, requestHeaders, application);
				application.processConnection(connection, null);
			} catch (Exception e) {
				resourceThread.onError();
				reload();
			}
		}
	}

	/**
	 * Resource loading thread
	 */
	private class SecondaryResourceFetchThread extends Thread {
		private BrowserContent browserField;
		private Vector imageQueue;
		private boolean done = false;
		private boolean error = false;
		private Object syncObject = new Object();
		private SecondaryResourceFetchThread currentThread;
		private AdserverBase application;

		/**
		 * Enqueues secondary resource for a browser field.
		 * 
		 * @param resource
		 *            - resource to retrieve.
		 * @param referrer
		 *            - call back browsr field.
		 */
		public void enqueue(RequestedResource resource, BrowserContent referrer) {
			if (null == resource) {
				return;
			}

			synchronized (syncObject) {
				if (currentThread == null) {
					currentThread = new SecondaryResourceFetchThread(application);
					currentThread.start();
				} else {
					if (!referrer.equals(currentThread.browserField)) {
						synchronized (currentThread.imageQueue) {
							currentThread.imageQueue.removeAllElements();
						}
					}
				}

				synchronized (currentThread.imageQueue) {
					currentThread.imageQueue.addElement(resource);
				}

				currentThread.browserField = referrer;
			}
		}

		SecondaryResourceFetchThread(AdserverBase application) {
			this.application = application;
			imageQueue = new Vector(0);
		}

		public void doneAddingImages() {
			synchronized (syncObject) {
				if (currentThread != null) {
					currentThread.done = true;
				} else {
					onLoaded();
				}
			}
		}

		public void onError() {
			synchronized (syncObject) {
				if (currentThread != null) {
					currentThread.error = true;
				} else {
					onLoaded();
				}
			}
		}

		private void onLoaded() {
			System.out.println("Content URL : "  + browserContent.getURL());
//			browserContent.getBrowserPageContext().get
//			System.out.println("Browser content: " + browserContent)
			AdserverLoadedNotify notify = new AdserverLoadedNotify(application, browserContent);
			Application.getApplication().invokeLater(notify);
		}

		private void onLoadingStart() {
			AdserverStartLoadingNotify notify = new AdserverStartLoadingNotify(application, url);
			Application.getApplication().invokeLater(notify);

		}

		public void run() {
			onLoadingStart();
			while (true) {
				if (error) {
					currentThread = null;
					break;
				}
				if (done) {
					synchronized (syncObject) {
						synchronized (imageQueue) {
							if (imageQueue.isEmpty()) {
								currentThread = null;
								onLoaded();
								break;
							}
						}
					}
				}

				RequestedResource resource = null;

				synchronized (imageQueue) {
					if (!imageQueue.isEmpty()) {
						resource = (RequestedResource) imageQueue.elementAt(0);
						imageQueue.removeElementAt(0);
					}
				}

				if (resource != null) {
					HttpConnection connection = null;

					try {
						connection = makeConnection(resource.getUrl(),
								resource.getRequestHeaders(), application);
						resource.setHttpConnection(connection);
						if (browserField != null) {
							browserField.resourceReady(resource);
						}
					} catch (IOException e) {
					}
				}
			}
		}
	}

	private static String getUserAgent() {
		StringBuffer result = new StringBuffer(150);
		result.append("BlackBerry").append(DeviceInfo.getDeviceName())
				.append('/').append(DeviceInfo.getPlatformVersion());
		result.append(" Profile/").append(
				System.getProperty("microedition.profiles"));
		result.append(" Configuration/").append(
				System.getProperty("microedition.configuration"));
		result.append(" VendorID/").append(Branding.getVendorId());
		return result.toString();
	}

	// private synchronized void setCacheEnabled(boolean enabled) {
	// cacheEnabled = enabled;
	// }

	public String getHashId() {
		return hashId;
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public String getDefaultImage() {
		return defaultImage;
	}

	// TODO TEST Disabled cache mode
	// public synchronized void addCacheItem(final String url, final String
	// urlHash) {
	// index.put(url, urlHash);
	// }

	// TODO TEST Disabled cache mode
	// public synchronized String getCacheFilename(final String url) {
	// return CacheManager.getMD5Hash(url);
	// }

	// TODO TEST Disabled cache mode
	// public boolean isCached(final String url) {
	// String cacheName = CacheManager.getMD5Hash(url);
	// FileConnection conn = null;
	// try {
	// conn = (FileConnection)
	// Connector.open(CacheManager.getInstance().getCachepath() + getHashId() +
	// '/' + cacheName, Connector.READ_WRITE);
	// } catch (IOException e) {
	// return false;
	// }
	// return !(!conn.exists() || conn.totalSize() == 0);
	// }

	// from Observable.java class
	// ////////////////////////////////////////////////////////////////////////
	private EventListener[] listeners = new EventListener[0];

	/**
	 * Set form event listener.
	 * @param listener
	 */
	public void addListener(EventListener listener) {
		if (listeners.length == 0) {
			listeners = new EventListener[1];
			listeners[0] = listener;
		} else {
			for (int i = 0; i < listeners.length; ++i) {
				if (listeners[i].equals(listener)) {
					return;
				}
			}

			EventListener[] listenersBuf = new EventListener[listeners.length + 1];
			System.arraycopy(listeners, 0, listenersBuf, 0, listeners.length);
			listenersBuf[listeners.length] = listener;
			listeners = listenersBuf;
		}
	}

	public void onLoaded() {
		for (int i = 0; i < listeners.length; ++i) {
			listeners[i].adDownloadEnd();
		}
	}

	public void onError(final String msg) {
		for (int i = 0; i < listeners.length; ++i) {
			listeners[i].adDownloadError(msg);
		}
	}

	public void onStartLoading() {
		for (int i = 0; i < listeners.length; ++i) {
			listeners[i].adDownloadBegin();
		}
	}
	// /////////////////////////////////////////////////////////////////
	// Getters - setters
//	/**
//	 * Optional.
//	 * Get URL of ad server.
//	 */
//	public String getAdServerUrl () {
//		if (null != request) {
//			return request.getAdServerUrl();
//		} else return null;
//	}
//
//	/**
//	 * Optional.
//	 * Overrides the URL of ad server.
//	 * @param adserverURL
//	 */
//	public void setAdServerUrl(String adServerUrl) {
//		if (null != request) {
//			request.setAdServerUrl(adServerUrl);
//		} 
//	}

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
	 * Set banner refresh interval (in seconds).
	 */
	public void setUpdateTime(int reloadPeriod) {

		if (reloadPeriod > 0) {
			synchronized (runLock) {
				runLock.notify();
			}
		}
		this.adReloadPreiod = reloadPeriod * 1000;
	}
	
	/**
	 * Set the flag which operates advertising opening.
	 * @param internalBrowser
	 */
	public void setInternalBrowser (boolean internalBrowser) {
		this.adInternalBrowserEnabled = internalBrowser;
	}
	
	/**
	 * Optional.
	 * Set image resource which will be shown during advertising loading if there is no advertising in a cache.
	 */
	public void setDefaultImage (String defaultImage) {
		this.defaultImage = defaultImage;
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

	protected void onDisplay() {
		System.out.println("Web View Added to screen");
		PrimaryResourceFetchThread cacheThread;
		cacheThread = new PrimaryResourceFetchThread(DEFAULT_HTML, null, thisPtr);
		cacheThread.start();

		super.onDisplay();
	}
	
	protected void onUndisplay() {
		setUpdateTime(0);
		super.onUndisplay();
	}
 }
