package com.adserver.core;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class WebViewInterstitial extends MainScreen implements FieldChangeListener {

	private final static long STYLE = Manager.HORIZONTAL_SCROLL | Manager.VERTICAL_SCROLL | Manager.HORIZONTAL_SCROLLBAR | Manager.VERTICAL_SCROLLBAR | Field.FIELD_HCENTER;
	Adserver adserver;
	AdserverInterstitial adserverInterstitial;
	MainScreen thisScreen;
	ButtonField closeButton;
	boolean placeButon = false;
	Manager fullScreenManager;
	int closeButtonX, closeButtonY;
//	int webViewMarginX = 0;
//	int webViewMarginY = 0;
	boolean shiftLayout = false;
//	int marginX = 0;
//	int marginY = 0;
	
	public WebViewInterstitial(final Adserver adserver, final AdserverInterstitial adserverInterstitial) {
		super(STYLE);
		thisScreen = this;
		closeButton = new ButtonField("Close", ButtonField.CONSUME_CLICK);
		closeButton.setChangeListener(this);
		this.adserver = adserver;
		this.adserverInterstitial = adserverInterstitial;

		fullScreenManager = new Manager(USE_ALL_WIDTH | USE_ALL_HEIGHT) {

			protected void sublayout(int width, int height) {
				int screenWidth = Display.getWidth();
				int screenHeight = Display.getHeight();

				setPositionChild(adserver, 0, 0);
				layoutChild(adserver, screenWidth, screenHeight);

				// hide close button
				setPositionChild(closeButton, -200, -200);
				layoutChild(closeButton, width, height);


				int closeButtonWidth = closeButton.getWidth();
				int closeButtonHeight = closeButton.getHeight();
				int resizedScreenWidth = screenWidth - closeButtonWidth;
				int resizedScreeenHeight = screenHeight - closeButtonHeight;

				if (placeButon) {
					switch (adserverInterstitial.getCloseButtonPosition()) {
					case AdserverInterstitial.CLOSEBUTTONPOSITIONCENTER:
						setPositionChild(closeButton, (screenWidth / 2) - (closeButtonWidth / 2), (screenHeight / 2) - (closeButtonHeight / 2));
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONTOP:
						setPositionChild(closeButton, (screenWidth / 2) - (closeButtonWidth / 2), 0);
						if (shiftLayout) {
							adserver.setSize(screenWidth, resizedScreeenHeight);
							setPositionChild(adserver, 0, closeButtonHeight);
						}
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONBOTTOM:
						setPositionChild(closeButton, (screenWidth / 2) - (closeButtonWidth / 2), screenHeight - closeButtonHeight);
						if (shiftLayout) {
							adserver.setSize(screenWidth, resizedScreeenHeight);
							setPositionChild(adserver, 0, 0);
						}
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONLEFT:
						setPositionChild(closeButton, 0, (screenHeight / 2) - (closeButtonHeight / 2));
						if (shiftLayout) {
							adserver.setSize(resizedScreenWidth, screenHeight);
							setPositionChild(adserver, closeButtonWidth, 0);
						}
						break;
					case AdserverInterstitial.CLOSEBUTTONPOSITIONRIGHT:
						setPositionChild(closeButton, screenWidth - closeButtonWidth, (screenHeight / 2) - (closeButtonHeight / 2));
						if (shiftLayout) {
							adserver.setSize(resizedScreenWidth, screenHeight);
							setPositionChild(adserver, 0, 0);
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
		fullScreenManager.add(adserver);
		fullScreenManager.add(closeButton);
		add(fullScreenManager);

		new Thread() {
			public void run() {
				try {
					Thread.sleep(adserverInterstitial.getShowCloseButtonTime());
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
		if (adserverInterstitial.getAutoCloseInterstitialTime() > 0) {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(adserverInterstitial.getAutoCloseInterstitialTime());
						UiApplication.getUiApplication().invokeLater(new Runnable() {
							public void run() {
								try {
									UiApplication.getUiApplication().popScreen(thisScreen);
								} catch (Exception e) {
								}
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
//			adserver.setUpdateTime(0);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected boolean keyDown(int keycode, int time) {
		// TODO Auto-generated method stub
		 if (Keypad.KEY_ESCAPE == Keypad.key(keycode)) {
			 return true;
		 }
		 return false;
	}
//	public boolean onMenu(int instance){
//        if( instance == Menu.INSTANCE_CONTEXT ){
//            //Retrieve the main menu (for some reason, the context menu cannot handle image hyperlinks)
//            Menu mainMenu = this.getMenu( Menu.INSTANCE_DEFAULT );
//            
//            int numItems = mainMenu.getSize();
//            for( int i = 0; i < numItems; i++ ){
//                MenuItem curItem = mainMenu.getItem(i);
//                String name = curItem.toString();
//                if( name.equalsIgnoreCase( "Open Link" ) ){                    
//                    curItem.run();                    
//                    break;
//                }
//            }
//            return false;
//        } else
//            return super.onMenu(instance);
//    }

	public void onEmptyContent() {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				try {
					UiApplication.getUiApplication().popScreen(thisScreen);
				} catch (Exception e) {
				}
			};
		});
	}
}
