package adsevertest;

import java.util.Hashtable;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

import com.adserver.core.AdClickListener;
import com.adserver.core.Adserver;
import com.adserver.utils.EventListener;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class AdserverScreen extends MainScreen implements EventListener, AdClickListener {
	private Adserver field;

	/**
	 * Creates a new MyScreen object
	 */
	public AdserverScreen() {
		setTitle("Adserver Example");

		Hashtable customParameters = new Hashtable();
		customParameters.put("key1", "value1");
		customParameters.put("key2", "value2");
		
		field = new Adserver("8061", "16112");
		field.setAdServerUrl("http://ads.mocean.mobi/ad?");
		field.setInternalBrowser(false);
		field.setCustomParameters(customParameters);
		field.setMaxSizeX(new Integer(300));
		field.addListener(this);
		field.setClickListener(this);
		field.setSize(360, 50);
		field.setUpdateTime(2);

		add(field);
	}

	public void adDownloadEnd() {
		//Your Ad download End handle code
	}
	
	public void adDownloadError(String error) {
		//Your Ad download Error handle code
		Dialog.alert(error);
		System.exit(-1);
	}

	public void adDownloadBegin() {
		//Your Ad download Begin handle code
		System.out.println("Downloading begin");
	}

	public synchronized boolean didAdClicked(final String url) {
		//Your Ad Clicked handle code
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.alert("Ad clicked!");

			};
		});
		return false;
	}
}
