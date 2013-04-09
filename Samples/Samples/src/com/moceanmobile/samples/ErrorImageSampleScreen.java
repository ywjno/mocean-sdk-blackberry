package com.moceanmobile.samples;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;

import com.moceanmobile.mast.MASTAdView;

public class ErrorImageSampleScreen extends SampleScreen
{
	public void onDownloadError(MASTAdView sender, String message, Exception error)
	{
		if (sender != adView)
			return;
		
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				Bitmap bitmap = Bitmap.getBitmapResource("errorImage.png");
				adView.getBitmapField().setBitmap(bitmap);
				
				if (adView.getBitmapField().getManager() == null)
					adView.add(adView.getBitmapField());
			}
		});
	}
}
