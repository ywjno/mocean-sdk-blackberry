package com.adserver.core;

import com.adserver.video.VideoField;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.FlowFieldManager;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class WebView extends FlowFieldManager {
	private final static long STYLE = Manager.HORIZONTAL_SCROLL | Manager.VERTICAL_SCROLL | Manager.HORIZONTAL_SCROLLBAR | Manager.VERTICAL_SCROLLBAR | Manager.FOCUSABLE;

	protected int width = Display.getWidth();
	protected int height = Display.getHeight();
	private boolean sizeSet = false;

	public BrowserField browserField = null;
	public VideoField videoField = null;

	/**
	 * Constructor
	 */
	public WebView() {
		super(STYLE);
	}

	public void sublayout(int maxWidth, int maxHeight) {
		try {
//			super.sublayout(maxWidth, maxHeight);
			if (!sizeSet) {
				super.sublayout(maxWidth, maxHeight);
				if (null != browserField) setExtent(browserField.getExtent().width, browserField.getExtent().height);
			}
			else {
				super.sublayout(width, height);
				setExtent(width, height);
			}
		} catch (Exception e) {
		}
	}

	public int getPreferredWidth() {
		return width;
	}

	public int getPreferredHeight() {
		return height;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		sizeSet = true;
		super.sublayout(width, height);
	}
	
//	public void deleteAll() {
//		synchronized (UiApplication.getEventLock()) {
//			try{
//				deleteAll();
//			}catch (Exception e) {
//			}
//		}
//	}
//	public void addBrowserField() {
//		synchronized (UiApplication.getEventLock()) {
//			try{
//				add(browserField);
//			}catch (Exception e) {
//			}
//		}
//	}
	
//	public void displayBrowserField() {
//		synchronized (UiApplication.getEventLock()) {
//			try{
//				if (getFieldCount() >0) {
//					deleteAll();
//				}
//				add(browserField);
//			}catch (Exception e) {
//			}
//		}
//	}
	
//	public void displayBrowserField() {
//		if ((this.getFieldCount() > 0)) {
//			Application.getApplication().invokeAndWait(new Runnable() {
//				public void run() {
//					deleteAll();
//					Application.getApplication().invokeAndWait(new Runnable() {
//						public void run() {
//							add(browserField);
//						}
//					});
//				}
//			});
//
//		} else {
//			Application.getApplication().invokeAndWait(new Runnable() {
//				public void run() {
//					add(browserField);
//				}
//			});
//		}
//	}
	public void displayVideoField(final String fileName,final int width,final int height, final AdClickListener clickListener, final String url, AdserverBase adserver) {
		videoField = new VideoField(fileName, width, height, clickListener, url, adserver);
		Application.getApplication().invokeAndWait(new Runnable() {
			public void run() {
				try {
					deleteAll();
				} catch (Exception e) {
				}
				add(videoField);
			}
		});
	}
}
