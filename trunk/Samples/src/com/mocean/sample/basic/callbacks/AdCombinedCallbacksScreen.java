package com.mocean.sample.basic.callbacks;

import com.MASTAdview.core.AdClickListener;
import com.MASTAdview.core.MASTAdview;


import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class AdCombinedCallbacksScreen  extends MainScreen  implements com.MASTAdview.core.EventListener, AdClickListener{
	AdCombinedCallbacksScreen thisPtr;
	MASTAdview field;
		
	public AdCombinedCallbacksScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		thisPtr = this;
		setTitle("adCombinedCallbacks Sample");
		
		field = new MASTAdview(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.addListener(this);
		field.setClickListener(this);
		add(field);
		add(new LabelField("Callback fired:"));
	}

	public void onError(String arg0) {
		thisPtr.add(new LabelField("Error callback : " + arg0));
		
	}

	public void onLoaded() {
		thisPtr.add(new LabelField("Ad download ended"));
		
	}

	public void onStartLoading() {
		thisPtr.add(new LabelField("Ad download started"));
	}

	public boolean didAdClicked(String url) {
		thisPtr.add(new LabelField("Ad clicked by user"));
		return false;
	}

}