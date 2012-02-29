package com.mocean.sample.basic.callbacks;

import com.MASTAdview.core.AdClickListener;
import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class AdClickedHandledByUserScreen extends MainScreen implements AdClickListener{
	AdClickedHandledByUserScreen thisPtr;
	MASTAdview field;
	
	public AdClickedHandledByUserScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		thisPtr = this;
		setTitle("Ad Click callback Sample");
		field = new MASTAdview(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.setClickListener(this);
		add(field);
		add(new LabelField("Ad clicks consumed by user callback:"));


	}

	public boolean didAdClicked(String arg0) {
		thisPtr.add(new LabelField("Banner Clicked"));
		return true;
	}
	

}
