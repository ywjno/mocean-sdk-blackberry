package com.mocean.sample.basic;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;

import com.mocean.sample.ListScreen;
import com.mocean.sample.basic.callbacks.*;

public class SimpleAdCallbackListScreen extends ListScreen{
	
	public SimpleAdCallbackListScreen() {
		super();
		setTitle("mOcean Samples");

		add(new LabelField("Callbacks List:", LabelField.FIELD_HCENTER));
		add(new SeparatorField());
		_listField = new ListField();
		ListCallBack _callback = new ListCallBack();
		_listField.setCallback(_callback);

		_listField.insert(0);
		_callback.insert("adDownloadBegin", 0);
		
		_listField.insert(1);
		_callback.insert("adDownloadEnd", 1);

		_listField.insert(2);
		_callback.insert("adDownloadError", 2);

		_listField.insert(3);
		_callback.insert("Ad click opened in browser", 3);

		_listField.insert(4);
		_callback.insert("Ad click handled by user", 4);

		_listField.insert(5);
		_callback.insert("Combined callback sample", 5);

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
					UiApplication.getUiApplication().pushScreen(new AdDownloadBeginScreen());
					break;
				case 1:
					UiApplication.getUiApplication().pushScreen(new AdDownloadEndScreen());
					break;
				case 2:
					UiApplication.getUiApplication().pushScreen(new AdDownloadErrorScreen());
					break;
				case 3:
					UiApplication.getUiApplication().pushScreen(new AdClickedBrowserScreen());
					break;
				case 4:
					UiApplication.getUiApplication().pushScreen(new AdClickedHandledByUserScreen());
					break;
				case 5:
					UiApplication.getUiApplication().pushScreen(new AdCombinedCallbacksScreen());
					break;
				}
			}
		}
	};
}
