package com.adserver.utils;

import com.adserver.core.WebView;
import com.adserver.core.WebViewInterstitial;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class Observable {
	private EventListener[]	listeners	= new EventListener[0];

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

}
