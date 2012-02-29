package com.mocean.sample.basic.callbacks;

import java.util.Enumeration;
import java.util.Hashtable;

import com.MASTAdview.core.MASTAdview;
import com.MASTAdview.core.OnThirdPartyRequest;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

public class AdThirdPartyCallbackScreen extends MainScreen implements OnThirdPartyRequest{
	AdThirdPartyCallbackScreen thisPtr;
	MASTAdview field;
	
	public AdThirdPartyCallbackScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		setTitle("AdThirdPartyCallback Sample");
		
		field = new MASTAdview(8061, 93111);
//		field = new Adserver(8061, 16938);

		field.setAdServerUrl("http://192.168.1.162/new_mcn/request.php");
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.setThirdPartyListener(this);
		add(field);
	}
	
	public void event(Hashtable params) {
		
		String param = "Params : \n";
		Enumeration e = params.keys();
	    while(e.hasMoreElements()){
		    String key  =  (String) e.nextElement();
			String value = (String)params.get(key);
			param = param + key + " : " + value + "\n";
	    }
		Dialog.alert(param);

	}

}
