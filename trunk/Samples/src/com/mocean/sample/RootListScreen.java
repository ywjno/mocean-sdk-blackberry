package com.mocean.sample;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;


public class RootListScreen extends ListScreen{
	
	public RootListScreen() {
		super();
		
		add(new LabelField("Please select sample type:",
				LabelField.FIELD_HCENTER));
		add(new SeparatorField());

		_listField = new ListField();
		ListCallBack _callback = new ListCallBack();
		_listField.setCallback(_callback);

		_listField.insert(0);
		_callback.insert("Basic", 0);
		_listField.insert(1);
		_callback.insert("Advanced", 1);
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
					UiApplication.getUiApplication().pushScreen(new BasicListScreen());
					break;
				case 1:
					UiApplication.getUiApplication().pushScreen(new AdvancedListScreen());
					break;
				}
			}
		}
	};
	


}
