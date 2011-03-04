package com.adserver.core;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.FlowFieldManager;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public final class WebView extends FlowFieldManager {
	private final static long STYLE = Manager.HORIZONTAL_SCROLL | Manager.VERTICAL_SCROLL | Manager.HORIZONTAL_SCROLLBAR | Manager.VERTICAL_SCROLLBAR;
	private int width = Display.getWidth();
	private int height = Display.getHeight();
	private boolean sizeSet = false;

	private Field browserField;
	public static WebView instance;

	/**
	 * Constructor
	 */
	public WebView() {
		super(STYLE);
		instance = this;
	}

	public static WebView getInstance() {
		if (null == instance) {
			instance = new WebView();
		}
		return instance;
	}


	/**
	 * Init webview with browser content
	 * 
	 * @param content
	 *            Browser content
	 */
	public void setContent(BrowserContent content) {
		deleteAll();
		browserField = content.getDisplayableContent();
		add(browserField);
	}

	public void sublayout(int maxWidth, int maxHeight) {
		try {
			super.sublayout(maxWidth, maxHeight);
			if (!sizeSet) setExtent(browserField.getExtent().width, browserField.getExtent().height);
			else setExtent(width, height);
		} catch (Exception e) {
			// TODO: handle exception
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
}
