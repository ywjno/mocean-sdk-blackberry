package com.adserver.common;

import com.adserver.core.AdClickListener;
import com.adserver.core.AdserverInterstitial;
import com.adserver.core.AdserverRequest;
import com.adserver.utils.EventListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class Main extends UiApplication implements EventListener, AdClickListener {
	private MainScreen mainScreen;
	private AdserverInterstitial field;
	
	public static void main(String[] args) {
		Main app = new Main();
		app.enterEventDispatcher();
	}

	private Main() {
		mainScreen = new MainScreen() {
			public boolean onClose() {
				close();
				return true;
			}
		};
		mainScreen.add(new LabelField("First screen"));
		pushScreen(mainScreen);

		field = new AdserverInterstitial("571", "345", 
				AdserverRequest.ADS_TYPE_IMAGES_ONLY, "5644", "7943", null, null, 
				null, new Integer(0), null, null, null, null, null, null, 
				null,null, null, null, null, null, null, null, null, null, 
				"test", "defaultImage", Boolean.TRUE, this, null, 0, true, 1000, 0, "name1;value1;name2;value2");
		field.addListener(this);
		field.setUpdateTime(2000);
    }

	public void adDownloadEnd() {
		// TODO Auto-generated method stub
		System.out.println("Download ended");
	}

	public void adDownloadError(String error) {
		// TODO Auto-generated method stub
		System.out.println("Download error");
	}

	public void adDownloadBegin() {
		// TODO Auto-generated method stub
		System.out.println("Download begin");
	}
	public synchronized boolean didAdClicked(String url) {
		// TODO Auto-generated method stub
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.alert("Banner clicked");
			};
		});
		return false;
	}
}