package com.mocean.sample.advanced;

import java.io.IOException;

import com.MASTAdview.core.DataRequest;
import com.MASTAdview.core.MASTAdviewRequest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class DataRequestScreen extends MainScreen {
	
	public DataRequestScreen() {
		super();
		setTitle("DataRequest Sample");
		
		add(new LabelField("Response from server:"));
		//Create AddserverRequest object
		MASTAdviewRequest request = new MASTAdviewRequest("8061", "20249");
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
}
