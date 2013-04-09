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
