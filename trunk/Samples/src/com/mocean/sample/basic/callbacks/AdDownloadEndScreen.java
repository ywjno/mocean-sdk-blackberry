package com.mocean.sample.basic.callbacks;

import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class AdDownloadEndScreen extends MainScreen implements com.MASTAdview.core.EventListener{
	AdDownloadEndScreen thisPtr;
	MASTAdview field;
	
	public AdDownloadEndScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		thisPtr = this;
		setTitle("adDownloadEnd Sample");
		
		field = new MASTAdview(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.addListener(this);
		add(field);
		add(new LabelField("Callback fired:"));

	} 

	public void onError(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onLoaded() {
		// TODO Auto-generated method stub
		thisPtr.add(new LabelField("Ad download finished"));
		
	}

	public void onStartLoading() {
		// TODO Auto-generated method stub
		
	}

}