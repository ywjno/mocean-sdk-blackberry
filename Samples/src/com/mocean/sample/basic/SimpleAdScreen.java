package com.mocean.sample.basic;

import com.adserver.core.Adserver;
import net.rim.device.api.ui.container.MainScreen;

public class SimpleAdScreen extends MainScreen{
	
	public SimpleAdScreen() {
		super();
		setTitle("Simple Ad Sample");
		
		Adserver field = new Adserver(8061, 20249);
		field.setSize(360,50);
		field.setUpdateTime(10);
		add(field);

	}

}
