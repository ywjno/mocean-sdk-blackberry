package com.mocean.sample;

import com.mocean.sample.basic.*;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;

public class BasicListScreen extends ListScreen {
	
	public BasicListScreen() {
		super();
		setTitle("mOcean Samples");

		add(new LabelField("Supported features:", LabelField.FIELD_HCENTER));
		add(new SeparatorField());
		
		_listField = new ListField();
		ListCallBack _callback = new ListCallBack();
		_listField.setCallback(_callback);

		_listField.insert(0);
		_callback.insert("Simple Ad", 0);
		
		_listField.insert(1);
		_callback.insert("Interstitial Ad", 1);

		_listField.insert(2);
		_callback.insert("User Location auto detect", 2);

		_listField.insert(3);
		_callback.insert("Orientations support", 3);

		_listField.insert(4);
		_callback.insert("Callback notification", 4);

		_listField.insert(5);
		_callback.insert("Default ad (set default image)", 5);

		_listField.insert(6);
		_callback.insert("Test mode enabled", 6);

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
				//Simple Ad
				case 0:
					UiApplication.getUiApplication().pushScreen(new SimpleAdScreen());
					break;
				//Interstitial Ad List
				case 1:
					UiApplication.getUiApplication().pushScreen(new InterstitialListScreen());
					break;
				case 2:
					UiApplication.getUiApplication().pushScreen(new SimpleAdLocationScreen());
					break;
				case 3:
					UiApplication.getUiApplication().pushScreen(new SimpleAdRotateScreen());
					break;
				case 4:
					UiApplication.getUiApplication().pushScreen(new SimpleAdCallbackListScreen());
					break;
				case 5:
					UiApplication.getUiApplication().pushScreen(new SimpleAdDefaultImageScreen());
					break;
				case 6:
					UiApplication.getUiApplication().pushScreen(new SimpleAdTestModeScreen());
					break;
				}
			}
		}
	};

}
