package com.mocean.sample.basic.callbacks;

import com.MASTAdview.core.MASTAdview;


import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class AdDownloadErrorScreen extends MainScreen  implements com.MASTAdview.core.EventListener{
	AdDownloadErrorScreen thisPtr;
	MASTAdview field;
	
	public AdDownloadErrorScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		thisPtr = this;
		setTitle("adDownloadError Sample");
//		This combination of site/zone will always return error - invalid params
		field = new MASTAdview(0, 0);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.addListener(this);
		add(field);
		add(new LabelField("Callback fired:"));

		
	}

	public void onError(String arg0) {
		// TODO Auto-generated method stub
		thisPtr.add(new LabelField("Error callback: " + arg0));
		
	}

	public void onLoaded() {
		// TODO Auto-generated method stub
		
	}

	public void onStartLoading() {
		// TODO Auto-generated method stub
		
	}
	
	

}
