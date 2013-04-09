package com.moceanmobile.samples;

import net.rim.device.api.ui.UiApplication;

import com.moceanmobile.mast.MASTAdView;

public class ErrorHideSampleScreen extends SampleScreen
{
	public void onAdReceived(MASTAdView sender)
	{
		if (sender != adView)
			return;

		if ((interstitial == false) && (adView.getManager() == null))
			insert(adView, 0);
		
		super.onAdReceived(sender);
	}

	public void onDownloadError(MASTAdView sender, String message, Exception error)
	{
		if (sender != adView)
			return;
		
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				delete(adView);
			}
		});
	}
}
