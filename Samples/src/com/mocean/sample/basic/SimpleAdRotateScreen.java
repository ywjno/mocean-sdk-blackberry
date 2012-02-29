package com.mocean.sample.basic;

import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class SimpleAdRotateScreen extends MainScreen{
	
	public SimpleAdRotateScreen() {
		super();
		setTitle("Simple Ad Rotation Sample");
		
		MASTAdview field = new MASTAdview(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(10);
		add(field);
		add(new LabelField("Please rotate your phone in landscape position"));

	}

}