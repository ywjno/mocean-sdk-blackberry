package com.mocean.sample.basic;

import com.adserver.core.Adserver;
import com.adserver.core.EventListener;


import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class SimpleAdLocationScreen extends MainScreen  implements EventListener{
	SimpleAdLocationScreen thisPtr;
	Adserver field;
	
	public SimpleAdLocationScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		thisPtr = this;
		setTitle("Carier Autodetection Sample");
		
		field = new Adserver(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.addListener(this);
		add(field);
		add(new LabelField("Please wait till GPS coorginates detected"));
		add(new LabelField("Coordinates:"));
	}


	public void onError(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onLoaded() {
		// TODO Auto-generated method stub
		//Update screen with coordinates
		thisPtr.add(new LabelField("Latitude: " + field.getLatitude() + " Longitude: " + field.getLongitude()));
		
	}

	public void onStartLoading() {
		// TODO Auto-generated method stub
		
	}

}
