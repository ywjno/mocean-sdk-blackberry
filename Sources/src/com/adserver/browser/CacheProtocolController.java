package com.adserver.browser;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;

import com.adserver.core.AdClickListener;
import com.adserver.core.AdserverBase;
import com.adserver.core.DataRequest;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.browser.field2.ProtocolController;
import net.rim.device.api.system.Application;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
public class CacheProtocolController extends ProtocolController{

	// The BrowserField instance
    private BrowserField browserField;
    
    // CacheManager will take care of cached resources 
    private CacheManager cacheManager;

    // click listener instance
    AdClickListener clickListener;
    
    // get url
    String trackUrl = null;
    
    private AdserverBase adserver;

	public CacheProtocolController(BrowserField browserField, AdClickListener clickListener, String trackUrl, AdserverBase adserver) {
		super(browserField);
		this.browserField = browserField;
		this.clickListener = clickListener;
		this.trackUrl = trackUrl;
		this.adserver = adserver;
	}
	
	private CacheManager getCacheManager() {
		if ( cacheManager == null ) {
			cacheManager = new CacheManagerImpl();
		}
		return cacheManager;
	}
	

	/**
	 * Handle navigation requests (e.g., link clicks)
	 */
	public void handleNavigationRequest(final BrowserFieldRequest request) throws Exception {
		try {
			//Send GET request to track url
			if (null != trackUrl) {
				Thread send = new Thread() {
					public void run() {
						try {
							DataRequest.getResponse(trackUrl);
						} catch (Exception e) {
						}
					}
				};
				send.run();
				
			}
			if (null != clickListener) {
				Application.getApplication().invokeLater(new Runnable() {
					public void run() {
						if (!clickListener.didAdClicked(request.getURL())) {
							final String url = request.getURL();
							adserver.getLogger().info(" CacheProtocolController - Link clicked: "+ url);
							Browser.getDefaultSession().displayPage(url);
						}

					}
				});
			} else {
				Application.getApplication().invokeLater(new Runnable() {
					public void run() {
						final String url = request.getURL();
						adserver.getLogger().info(" CacheProtocolController - Link clicked: "+ url);
						Browser.getDefaultSession().displayPage(url);
					}
				});
			}
		} catch (Exception e) {
		}
		
//		InputConnection ic = handleResourceRequest(request);
//		browserField.displayContent(ic, request.getURL());
	}

	/**
	 * Handle resource request (e.g., images, external css/javascript resources)
	 */
	public InputConnection handleResourceRequest(BrowserFieldRequest request) throws Exception {
		adserver.getLogger().info(" CacheProtocolController - Resourse Url : " + request.getURL());
		// if requested resource is cacheable (e.g., an "http" resource), use the cache
		if (getCacheManager() != null && getCacheManager().isRequestCacheable(request)) {
			InputConnection ic = null;
            // if requested resource is cached, retrieve it from cache
            if (getCacheManager().hasCache(request.getURL()) && !getCacheManager().hasCacheExpired(request.getURL())) {
            	ic = getCacheManager().getCache(request.getURL());
            }
            // if requested resource is not cached yet, cache it
            else {
            	ic = super.handleResourceRequest(request);
                if (ic instanceof HttpConnection) {
                    HttpConnection response = (HttpConnection) ic;
                    if (getCacheManager().isResponseCacheable(response)) {
                        ic = getCacheManager().createCache(request.getURL(), response);
                    }
                }
            }
            return ic;
		}
		// if requested resource is not cacheable, load it as usual
		return super.handleResourceRequest(request);
	}
}
