package com.moceanmobile.mast;

import java.util.Hashtable;

/**
 * Delegate interface for responding to MASTAdView instance actions.
 * 
 * Warning: These methods are not guaranteed to be invoked from the
 * application/UI thread.  Any interaction with UI components (including
 * the sender MASTAdView) MUST be dispatched with the main thread via 
 * UIApplication's invokeAndWait or invokeLater methods.
 * 
 */
public interface MASTAdViewHandler
{
	/**
	 * Indicates that an ad has been received and rendered.
	 * 
	 * @param sender MASTAdView instance invoking the callback.
	 */
	void onAdReceived(MASTAdView sender);
	
	/**
	 * The MASTAdView instance has encountered an error and can not render an ad.
	 * The ad currently displayed (if there is one) will remain in place.
	 * 
	 * @param sender MASTAdView instance invoking the callback.
	 * @param message Description of the error.  Can be null.
	 * @param error Encountered exception that caused the error.  Can be null.
	 */
	void onDownloadError(MASTAdView sender, String message, Exception error);
	
	/**
	 * Indicates the ad has been clicked.
	 * 
	 * If the application chooses to inspect or otherwise handle the URL then 
	 * the application should return false to prevent the SDK from also handling
	 * the URL.
	 * 
	 * @param sender MASTAdView instance invoking the callback.
	 * @param url The URL to present when the user clicks on the ad.  This URL may
	 * be a redirect URL.
	 * @return true allows the SDK to open the URL as configured (internally or with the
	 * device's browser).
	 * false halts opening of the URL.
	 */
	boolean onAdClicked(MASTAdView sender, String url);
	
	/**
	 * Indicates that the SDK has opened the internal browser to handle the URL
	 * provided by the onAdClicked callback.
	 * 
	 * Applications may want to suspend application specific activity while the
	 * user is interacting with the internal browser.
	 * 
	 * @param sender MASTAdView instance invoking the callback.
	 */
	void onInternalBrowserOpen(MASTAdView sender);
	
	/**
	 * Indicates that the SDK has closed the internal browser.  This may happen
	 * because the user closed or backed out of the browser or if the MASTAdView 
	 * instance reset/removeContent method was invoked.
	 * 
	 * @param sender MASTAdView instance invoking the callback.
	 */
	void onInternalBrowserClose(MASTAdView sender);
	
	/**
	 * Indicates that the ad server received a third party ad request from the ad network.
	 * 
	 * @param sender MASTAdView instance invoking the callback.
	 * @param properties Properties of the request.
	 * @param params Parameters for the third party SDK/network.
	 */
	void onThirdPartyEvent(MASTAdView sender, Hashtable properties, Hashtable params);
	
	/**
	 * Indicates a log event has been generated.
	 * 
	 * Logging in the SDK is done with System.out logging.  This callback is only invoked
	 * if the event is configured to log via MASTAdView's setLogLevel method.
	 * 
	 * @param sender MASTAdView instance invoking the callback.
	 * @param type The event type.
	 * @param message Log message.
	 * @param exception Exception causing the log entry.  Can be null.
	 * @return true to generate and output a the log entry to System.out.
	 * false to ignore the log event.
	 */
	boolean onLogEvent(MASTAdView sender, int type, String message, Exception exception);
}
