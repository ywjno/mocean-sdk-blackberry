package com.mocean.sample.basic;

import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class ManualRefreshScreen extends MainScreen{
	
	public ManualRefreshScreen() {
		super();
		setTitle("Manual refresh Sample");
		add(new LabelField("Press update to refresh", FOCUSABLE));

		final MASTAdview adserver = new MASTAdview(19829, 88269);
//		19829/ 88269
		adserver.setSize(360, 50);
		adserver.setUpdateTime(20);
		adserver.setLoggerId("Log1");
		adserver.setContentAlignment(true);
		add(adserver);
		
		ButtonField button = new ButtonField("Update", ButtonField.FIELD_HCENTER|ButtonField.FOCUSABLE|ButtonField.CONSUME_CLICK);
		button.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				adserver.update();
			}
		});
		add(button);
	}
	
	public boolean onClose(){
		setDirty(false);
		return super.onClose();
	}
}
