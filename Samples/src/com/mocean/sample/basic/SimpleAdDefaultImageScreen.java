package com.mocean.sample.basic;

import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.container.MainScreen;

public class SimpleAdDefaultImageScreen extends MainScreen{
	
	public SimpleAdDefaultImageScreen() {
		super();
		setTitle("setDefaultImage Sample");
		
		MASTAdview field = new MASTAdview(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.setDefaultImage("progress.gif");
		add(field);
	}
		
}
