package com.mocean.sample.advanced;

import com.adserver.core.Adserver;

import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

public class AdvancedAdUpdateScreen extends MainScreen{
	Adserver field;

	public AdvancedAdUpdateScreen() {
		super();
		setTitle("Using Update() Sample");
		
		field = new Adserver(8061, 20249);
		field.setSize(320,50);
		field.setUpdateTime(20);
		add(field);
		add(new SeparatorField());
		add(new LabelField("Reload period set to 20sec"));
		add(new LabelField("Click Update button to manualy reload Ad"));
		ButtonField button = new ButtonField("Update",ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY) {
			protected boolean touchEvent(TouchEvent event) {
				switch( event.getEvent()  ) {          
                case TouchEvent.CLICK:           
                  //handle click event here
                	field.update();
                  return true;
				}
				return false;
			}
		};
		add(button);
	}
}
