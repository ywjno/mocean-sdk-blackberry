package com.mocean.sample.advanced;

import com.adserver.core.AdserverRequest;
import com.adserver.core.DataRequest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class DataRequestScreen extends MainScreen {
	
	public DataRequestScreen() {
		super();
		setTitle("DataRequest Sample");
		
		add(new LabelField("Response from server:"));
		//Create AddserverRequest object
		Thread dataRequestThread = new Thread() {
			public void run() {
				AdserverRequest request = new AdserverRequest("8061", "20249");
				final String url = request.createURL();

				Application.getApplication().invokeAndWait(new Runnable() {
					public void run() {
						String response = null;
						try {
							response = DataRequest.getResponse(url);
						} catch (Exception e) {
							response  = "Error while fetching response";
						}
						add(new LabelField(response));
					}
				});
			}
		};
		dataRequestThread.start();
	}
}
