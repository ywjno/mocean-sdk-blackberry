package com.mocean.sample.basic;

import com.mocean.sample.ListScreen;
import com.mocean.sample.basic.interstitial.*;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;

public class InterstitialListScreen extends ListScreen{
	
	public InterstitialListScreen() {
		super();
		setTitle("Interstitial Control Samples List");

		add(new LabelField("Interstitial Control Features:", LabelField.FIELD_HCENTER));
		add(new SeparatorField());
		
		_listField = new ListField();
		ListCallBack _callback = new ListCallBack();
		_listField.setCallback(_callback);

		_listField.insert(0);
		_callback.insert("Simple Interstitial Control", 0);
		
		_listField.insert(1);
		_callback.insert("Close button in bottom position", 1);

		_listField.insert(2);
		_callback.insert("Close button delayed in 5 sec", 2);

		_listField.insert(3);
		_callback.insert("Auto close time = 10 sec", 3);

//		_listField.insert(4);
//		_callback.insert("Interstitial form rotate", 4);

		add(_listField);


	}
	
//	List click mechanics
	protected void makeMenu(Menu menu, int instance) {
		Field focusingOn = this.getLeafFieldWithFocus();
		if (focusingOn == _listField) {
			menu.add(_clickMenu);
		}
	}

	protected MenuItem _clickMenu = new MenuItem("click", 110, 10) {
		public void run() {
			int selectedIndex = -1;
			if (_listField == null) {
				selectedIndex = -1;
			} else {
				selectedIndex = _listField.getSelectedIndex();

				switch (selectedIndex) {
				case 0:
					new InterstitialAdLauncher();
					break;
				case 1:
					new InterstitialAdCloseButtonBottom();
					break;
				case 2:
					new InterstitialAdCloseButtonDelayed();
					break;
				case 3:
					new InterstitialAdAutoClose();
					break;
//				case 4:
//					break;
				
				}
			}
		}
	};
		


}
