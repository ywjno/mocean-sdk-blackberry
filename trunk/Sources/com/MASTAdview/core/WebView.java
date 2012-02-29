package com.MASTAdview.core;

import com.MASTAdview.video.VideoField;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.FlowFieldManager;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class WebView extends FlowFieldManager {
	private final static long STYLE = Manager.HORIZONTAL_SCROLL | Manager.VERTICAL_SCROLL | Manager.HORIZONTAL_SCROLLBAR | Manager.VERTICAL_SCROLLBAR | Manager.FOCUSABLE;

//	protected int width = Display.getWidth();
//	protected int height = Display.getHeight();
	protected int myWidth = 0;
	protected int myHeight = 0;
	public boolean sizeSet = false;
	public boolean autoCollapse = true;
	

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
			if (autoCollapse) {
				super.sublayout(0, 0);
				setExtent(0, 0);
				System.out.println("Sublayout with : " + 0 + "," + 0);
			} else {
				if (!sizeSet) {
					//DO nothing
//					super.sublayout(maxWidth, maxHeight);
//					if (null != browserField) setExtent(browserField.getExtent().width, browserField.getExtent().height);
				}
				else {
					super.sublayout(myWidth, myHeight);
					setExtent(myWidth, myHeight);
					System.out.println("Sublayout with : " + myWidth + "," + myHeight);
//				sizeSet = false;
				}
			}
		} catch (Exception e) {
		}
	}
//	public void sublayout(int maxWidth, int maxHeight) {
//		try {
//			System.out.println("maxWidth=" + maxWidth + " maxHeight=" +maxHeight );
//			width = maxWidth;
//			height = maxHeight;
//			super.sublayout(width, height);
//			setExtent(width, height);
//		} catch (Exception e) {
//		}
//	}

	public int getPreferredWidth() {
		return myWidth;
	}

	public int getPreferredHeight() {
		return myHeight;
	}

//	public void setSize(int width, int height) {
//		this.width = width;
//		this.height = height;
//		Constants.size_x = width;
//		Constants.size_y = height;
//		sizeSet = true;
//		super.sublayout(width, height);
//	}
	
	
	
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
