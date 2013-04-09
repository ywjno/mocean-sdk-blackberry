package com.moceanmobile.mast;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.ProtocolController;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class BrowserScreen extends FullScreen
{
	private BrowserField browserField = null;
	private final Handler handler;
	
	public BrowserScreen(Handler handler)
	{
		super(DEFAULT_MENU | DEFAULT_CLOSE);
		
		this.handler = handler;
		
		BrowserFieldConfig browserFieldConfig = new BrowserFieldConfig();
		browserField = new BrowserField(browserFieldConfig);
		
		BrowserController controller = new BrowserController(browserField);
		browserFieldConfig.setProperty(BrowserFieldConfig.CONTROLLER, controller);
		
		Manager manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL);
		manager.add(browserField);
		
		add(manager);
	}
	
	private class BrowserController extends ProtocolController
	{
		public BrowserController(BrowserField browserField)
		{
			super(browserField);
		}		
	}
	
	public void displayPage(String url)
	{
		browserField.requestContent(url);
	}
	
	protected boolean keyDown(int keycode, int time)
	{
		int key = Keypad.key(keycode);
		if  (key == Characters.ESCAPE)
		{
			if (browserField.back())
				return true;
		}
		
		return super.keyDown(keycode, time);
	}
	
	public boolean onClose()
	{
		boolean closed = super.onClose();
		
		if (closed && (handler != null))
		{
			handler.onBrowserScreenClose(this);
		}
		
		return closed;
	}
	
	public interface Handler
	{
		void onBrowserScreenClose(BrowserScreen sender);
	}
}
