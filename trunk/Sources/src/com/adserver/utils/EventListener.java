package com.adserver.utils;

import net.rim.device.api.ui.container.MainScreen;

import com.adserver.core.WebView;
import com.adserver.core.WebViewInterstitial;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public interface EventListener {
	/**
	 * @param webView
	 *            result field, placed on screen
	 */
	public void adDownloadEnd();
	public void adDownloadError(String error);
	public void adDownloadBegin();
}
