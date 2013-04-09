package com.moceanmobile.samples;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;

public class CustomSampleScreen extends SampleScreen
{
	protected void makeMenu(Menu menu, int instance)
	{
		menu.add(new CustomizeMenuItem());
		
		super.makeMenu(menu, instance);
	}
	
	protected class CustomizeMenuItem extends MenuItem
	{
		public CustomizeMenuItem()
		{
			super("Customize", 5, 0);
		}

		public void run()
		{
			if (adView == null)
				return;
			
			CustomDialog dialog = new CustomDialog();
			dialog.setAdWidth(adView.getAdWidth());
			dialog.setAdHeight(adView.getAdHeight());
			dialog.setUseInternalBrowser(adView.getUseInternalBrowser());
			
			int response = dialog.doModal();
			if (response == Dialog.OK)
			{
				adView.setAdWidth(dialog.getAdWidth());
				adView.setAdHeight(dialog.getAdHeight());
				adView.setUseInternalBrowser(dialog.getUseInternalBrowser());
				
				adView.update();
			}
		}
	}
}
