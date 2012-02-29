package com.MASTAdview.browser;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;

import net.rim.device.api.browser.field2.BrowserFieldRequest;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
public interface CacheManager {
    public boolean isRequestCacheable(BrowserFieldRequest request);
    public boolean isResponseCacheable(HttpConnection response);
    public boolean hasCache(String url);
    public boolean hasCacheExpired(String url);
    public InputConnection getCache(String url);
    public InputConnection createCache(String url, HttpConnection response);
    public String createCacheWithoutMetadata(String url, HttpConnection response);
    public void clearCache(String url);
	public String getCachepath();    
}
