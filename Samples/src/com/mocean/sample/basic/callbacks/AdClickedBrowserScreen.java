package com.mocean.sample.basic.callbacks;

import com.adserver.core.AdClickListener;
import com.adserver.core.Adserver;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class AdClickedBrowserScreen extends MainScreen implements AdClickListener{
	AdClickedBrowserScreen thisPtr;
	
	public AdClickedBrowserScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		thisPtr = this;
		setTitle("Ad Click callback Sample");
		Adserver field = new Adserver(8061, 16109);
		field.setClickListener(this);
		add(field);
		add(new LabelField("Click on Ad to open browser:"));
		
		
//		Adserver field = new Adserver(8061, 20249);
//		field.setSize(320,50);
//		field.setUpdateTime(5);
//		field.setClickListener(this);
//		add(field);
//		add(new LabelField("Click on Ad to open browser:"));
	}
	public boolean didAdClicked(String arg0) {
		return false;
	}
}
