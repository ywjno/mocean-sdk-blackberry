package com.adserver.core;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class WebViewInterstitial extends MainScreen implements FieldChangeListener {

	private final static long STYLE = Manager.HORIZONTAL_SCROLL | Manager.VERTICAL_SCROLL | Manager.HORIZONTAL_SCROLLBAR | Manager.VERTICAL_SCROLLBAR;

	MainScreen thisScreen;
	ButtonField closeButton;
	boolean placeButon = false;
	Manager fullScreenManager;
	int closeButtonX, closeButtonY;
	int webViewMarginX = 0;
	int webViewMarginY = 0;
	AdClickListener clickListener;
	WebView webView;

	public WebViewInterstitial(final int closeButtonPosition, final boolean shiftLayout, final int showCloseButtonTime, final int autoCloseInterstitialTime) {
		super(STYLE);
		thisScreen = this;
		closeButton = new ButtonField("Close", ButtonField.CONSUME_CLICK);
		closeButton.setChangeListener(this);
		this.clickListener = clickListener;
//		this.webView = WebView.getInstance();
		

		fullScreenManager = new Manager(USE_ALL_WIDTH | USE_ALL_HEIGHT) {

			protected void sublayout(int width, int height) {
				int screenWidth = Display.getWidth();
				int screenHeight = Display.getHeight();


				setPositionChild(webView, 0, 0);
				layoutChild(webView, screenWidth, screenHeight);

				// hide close button
				setPositionChild(closeButton, -200, -200);
				layoutChild(closeButton, width, height);


				int closeButtonWidth = closeButton.getWidth();
				int closeButtonHeight = closeButton.getHeight();
				int resizedScreenWidth = screenWidth - closeButtonWidth;
				int resizedScreeenHeight = screenHeight - closeButtonHeight;

				if (placeButon) {
					switch (closeButtonPosition) {
					case AdserverInterstitial.CLOSEBUTTONPOSITIONCENTER:
						setPositionChild(closeButton, (screenWidth / 2) - (closeButtonWidth / 2), (screenHeight / 2) - (closeButtonHeight / 2));
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONTOP:
						setPositionChild(closeButton, (screenWidth / 2) - (closeButtonWidth / 2), 0);
						if (shiftLayout) {
							webView.setSize(screenWidth, resizedScreeenHeight);
							setPositionChild(webView, 0, closeButtonHeight);
						}
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONBOTTOM:
						setPositionChild(closeButton, (screenWidth / 2) - (closeButtonWidth / 2), screenHeight - closeButtonHeight);
						if (shiftLayout) {
							webView.setSize(screenWidth, resizedScreeenHeight);
							setPositionChild(webView, 0, 0);
						}
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONLEFT:
						setPositionChild(closeButton, 0, (screenHeight / 2) - (closeButtonHeight / 2));
						if (shiftLayout) {
							webView.setSize(resizedScreenWidth, screenHeight);
							setPositionChild(webView, closeButtonWidth, 0);
						}
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONRIGHT:
						setPositionChild(closeButton, screenWidth - closeButtonWidth, (screenHeight / 2) - (closeButtonHeight / 2));
						if (shiftLayout) {
							webView.setSize(resizedScreenWidth, screenHeight);
							setPositionChild(webView, 0, 0);
						}
						break;
					default:
						setPositionChild(closeButton, (screenWidth / 2) - (closeButtonWidth / 2), (screenHeight / 2) - (closeButtonHeight / 2));
						break;
					}
				}
				setExtent(screenWidth, screenHeight);
			}
		};
		fullScreenManager.add(webView);
		fullScreenManager.add(closeButton);
		add(fullScreenManager);

		new Thread() {
			public void run() {
				try {
					Thread.sleep(showCloseButtonTime);
					UiApplication.getUiApplication().invokeLater(new Runnable() {
						public void run() {
							placeButon = true;
							updateLayout();
						};
					});
				} catch (InterruptedException ie) {
				}
			}
		}.start();
		if (autoCloseInterstitialTime > 0) {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(autoCloseInterstitialTime);
						UiApplication.getUiApplication().invokeLater(new Runnable() {
							public void run() {
								UiApplication.getUiApplication().popScreen(thisScreen);
							};
						});
					} catch (InterruptedException ie) {
					}
				}
			}.start();
		}
	}

	public void fieldChanged(Field field, int context) {
		try {
			UiApplication.getUiApplication().popScreen(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean onMenu(int instance){
        if( instance == Menu.INSTANCE_CONTEXT ){
            //Retrieve the main menu (for some reason, the context menu cannot handle image hyperlinks)
            Menu mainMenu = this.getMenu( Menu.INSTANCE_DEFAULT );
            
            int numItems = mainMenu.getSize();
            for( int i = 0; i < numItems; i++ ){
                MenuItem curItem = mainMenu.getItem(i);
                String name = curItem.toString();
                if( name.equalsIgnoreCase( "Open Link" ) ){                    
                    curItem.run();                    
                    break;
                }
            }
            return false;
        } else
            return super.onMenu(instance);
    }
	
}
