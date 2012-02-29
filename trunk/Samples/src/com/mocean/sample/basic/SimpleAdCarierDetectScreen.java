package com.mocean.sample.basic;

import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class SimpleAdCarierDetectScreen extends MainScreen{
	
	public SimpleAdCarierDetectScreen() {
		super();
		setTitle("Carrier Autodetection Sample");
		
		MASTAdview field = new MASTAdview(8061, 20249);
		field.setSize(320,50);
		add(field);
		add(new LabelField("Carier:"));
		add(new LabelField(field.getCarrier()));
	}

}
