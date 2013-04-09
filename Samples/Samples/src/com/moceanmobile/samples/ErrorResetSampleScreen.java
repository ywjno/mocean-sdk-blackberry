package com.moceanmobile.samples;

import net.rim.device.api.ui.UiApplication;

import com.moceanmobile.mast.MASTAdView;

public class ErrorResetSampleScreen extends SampleScreen
{
	public void onDownloadError(MASTAdView sender, String message, Exception error)
	{
		if (sender != adView)
			return;
		
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				adView.removeContent();
			}
		});
	}
}
