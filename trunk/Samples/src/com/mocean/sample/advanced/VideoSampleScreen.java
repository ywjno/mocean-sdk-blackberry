package com.mocean.sample.advanced;

import com.adserver.core.Adserver;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class VideoSampleScreen extends MainScreen{
	
	public VideoSampleScreen() {
		super();
		setTitle("Video Ad Sample");
		Adserver field = new Adserver(8061, 24514);
		
		field.setSize(360,200);
		field.setLogLevel(2);
		add(field);
		add(new LabelField("Your content here:", FOCUSABLE));
	}

}
