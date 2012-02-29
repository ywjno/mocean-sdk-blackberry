package com.MASTAdview.browser;

import net.rim.device.api.io.http.HttpHeaders;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
public class CacheItem {

	private String  url;	
	private long    expires;	
	private byte[] data;
	private HttpHeaders httpHeaders;
	
	public CacheItem(String url, long expires, byte[] data, HttpHeaders httpHeaders ) {
		this.url = url;
		this.expires = expires;
		this.data = data;
		this.httpHeaders = httpHeaders;
	}
	
	public String getUrl() {
		return url;
	}
	
	public long getExpires() {
		return expires;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}
}
