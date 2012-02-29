package com.mocean.sample.basic;

import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.container.MainScreen;

public class SimpleAdScreen extends MainScreen{
	public static volatile MainScreen thisPtr;
	
	public SimpleAdScreen() {
		super();
		thisPtr = this;
		
		setTitle("Simple Ad Sample");
		
		MASTAdview field = new MASTAdview(19829, 88269);
		
		field.setSize(360, 50);
		field.setUpdateTime(5);
		field.setLoggerId("Log1");
		field.setContentAlignment(true);
		add(field);
	}
}
