package com.mocean.sample;

import com.mocean.sample.advanced.*;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;

public class AdvancedListScreen extends ListScreen{
	
	public AdvancedListScreen() {
		super();
		setTitle("mOcean Samples");
		
		add(new LabelField("Advanced Samples list:", LabelField.FIELD_HCENTER));
		add(new SeparatorField());
		
		_listField = new ListField();
		ListCallBack _callback = new ListCallBack();
		_listField.setCallback(_callback);

		_listField.insert(0);
		_callback.insert("Orientation Change Sample", 0);

		_listField.insert(1);
		_callback.insert("Using Update() method", 1);
		
		_listField.insert(2);
		_callback.insert("Millenial SDK sample", 2);
		
		_listField.insert(3);
		_callback.insert("DataRequest.getResponse sample", 3);

		_listField.insert(4);
		_callback.insert("Video ad sample", 4);
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
					UiApplication.getUiApplication().pushScreen(new AdvancedAdOrientationScreen());
					break;
				case 1:
					UiApplication.getUiApplication().pushScreen(new AdvancedAdUpdateScreen());
					break;
				case 2:
					UiApplication.getUiApplication().pushScreen(new MillenialSampleScreen());
					break;
				case 3:
					UiApplication.getUiApplication().pushScreen(new DataRequestScreen());
					break;
				case 4:
					UiApplication.getUiApplication().pushScreen(new VideoSampleScreen());
					break;
				}
			}
		}
	};

}
