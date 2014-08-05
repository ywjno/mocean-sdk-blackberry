/*
 * PubMatic Inc. (“PubMatic”) CONFIDENTIAL
 * Unpublished Copyright (c) 2006-2014 PubMatic, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of PubMatic. The intellectual and technical concepts contained
 * herein are proprietary to PubMatic and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from PubMatic.  Access to the source code contained herein is hereby forbidden to anyone except current PubMatic employees, managers or contractors who have executed 
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes  
 * information that is confidential and/or proprietary, and is a trade secret, of  PubMatic.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, 
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF PubMatic IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE 
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS  
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.                
 */

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
