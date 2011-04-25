package com.adserver.core;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.FlowFieldManager;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class WebView extends FlowFieldManager {
	private final static long STYLE = Manager.HORIZONTAL_SCROLL | Manager.VERTICAL_SCROLL | Manager.HORIZONTAL_SCROLLBAR | Manager.VERTICAL_SCROLLBAR;

	private int width = Display.getWidth();
	private int height = Display.getHeight();
	private boolean sizeSet = false;

	public BrowserField browserField = null;
	/**
	 * Constructor
	 */
	public WebView() {
		super(STYLE);
	}

	public void sublayout(int maxWidth, int maxHeight) {
		try {
			super.sublayout(maxWidth, maxHeight);
			if (!sizeSet) setExtent(browserField.getExtent().width, browserField.getExtent().height);
			else setExtent(width, height);
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
	
	public void displayBrowserField() {
		if ((this.getFieldCount() > 0)) {
			Application.getApplication().invokeAndWait(new Runnable() {
				public void run() {
					deleteAll();
					Application.getApplication().invokeAndWait(new Runnable() {
						public void run() {
							add(browserField);
						}
					});
				}
			});

		} else {
			Application.getApplication().invokeAndWait(new Runnable() {
				public void run() {
					add(browserField);
				}
			});
		}
	}
}
