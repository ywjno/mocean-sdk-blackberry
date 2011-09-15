package com.mocean.sample.basic;

import com.adserver.core.Adserver;

import net.rim.device.api.ui.container.MainScreen;

public class SimpleAdTestModeScreen extends MainScreen{
	
	public SimpleAdTestModeScreen() {
		super();
		setTitle("Simple Ad Test Mode Sample");
		
		Adserver field = new Adserver(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.setTest(true);
		add(field);
	}
	
	

}
