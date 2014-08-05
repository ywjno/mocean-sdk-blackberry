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

package com.moceanmobile.samples;

import java.util.Hashtable;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.moceanmobile.mast.MASTAdView;

public class DelegateSampleScreen extends SampleScreen
{
	protected TextField textField = null;
	
	public DelegateSampleScreen()
	{
		textField = new TextField(Field.READONLY | Field.USE_ALL_WIDTH | Field.USE_ALL_HEIGHT);
		
		VerticalFieldManager manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		manager.add(textField);
		
		add(manager);
	}
	
	protected void addText(final String text)
	{
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				String newText = textField.getText();
				newText += text + "\n-----\n";
				textField.setText(newText);
				textField.setCursorPosition(newText.length());				
			}
		});
	}
	
	public void onAdReceived(MASTAdView sender)
	{
		addText(sender + ":onAdReceived");
		
		super.onAdReceived(sender);
	}

	public void onDownloadError(MASTAdView sender, String message, Exception error)
	{
		addText(sender + ":onDownloadError message:" + message + " error:" + error);
		
		super.onDownloadError(sender, message, error);
	}

	public boolean onAdClicked(MASTAdView sender, String url)
	{
		addText(sender + ":onAdClicked url:" + url);
		
		return super.onAdClicked(sender, url);
	}

	public void onInternalBrowserOpen(MASTAdView sender)
	{
		addText(sender + ":onInternalBrowserOpen");
		
		super.onInternalBrowserOpen(sender);
	}

	public void onInternalBrowserClose(MASTAdView sender)
	{
		addText(sender + ":onInternalBrowserClose");
		
		super.onInternalBrowserClose(sender);
	}

	public void onThirdPartyEvent(MASTAdView sender, Hashtable properties, Hashtable params)
	{
		addText(sender + ":onThirdPartyEvent properties:" + properties + " params:" + params);
		
		super.onThirdPartyEvent(sender, properties, params);
	}

	public boolean onLogEvent(MASTAdView sender, int type, String message, Exception exception)
	{
		addText(sender + ":onLogEvent type:" + type + " message:" + message + " exception:" + exception);
		
		return super.onLogEvent(sender, type, message, exception);
	}
}
