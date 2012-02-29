package com.mocean.sample.advanced;

import com.MASTAdview.core.AdClickListener;
import com.MASTAdview.core.EventListener;
import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class MillenialSampleScreen extends MainScreen implements EventListener, AdClickListener{
	MillenialSampleScreen thisPtr;
	
	public MillenialSampleScreen() {
		super();
		thisPtr = this;
		setTitle("Millenial SDK Sample");
		
		//Millenial sample zone
		MASTAdview field =  new MASTAdview(8061, 17324);
		field.setUpdateTime(5);
		field.addListener(this);
		field.setClickListener(this);
		add(field);
		add(new LabelField("Callback fired:"));
	}

	public void onError(String arg0) {
		thisPtr.add(new LabelField("Error callback : " + arg0));
		
	}

	public void onLoaded() {
		thisPtr.add(new LabelField("Ad download ended"));
		
	}

	public void onStartLoading() {
		thisPtr.add(new LabelField("Ad download started"));
	}

	public boolean didAdClicked(String url) {
		thisPtr.add(new LabelField("Ad clicked by user"));
		return false;
	}

}
