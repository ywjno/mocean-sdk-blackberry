package com.moceanmobile.samples;

import java.util.Hashtable;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdViewHandler;

public class SampleScreen extends MainScreen implements MASTAdViewHandler
{
	protected boolean interstitial = false;
	protected int site = 0;
	protected int zone = 0;
	
	protected MASTAdView adView = null;
	
	public SampleScreen()
	{
		super(DEFAULT_CLOSE | Manager.NO_HORIZONTAL_SCROLL | Manager.NO_VERTICAL_SCROLL);
		
		add(new SeparatorField());
	}
	
	protected void makeMenu(Menu menu, int instance)
	{
		menu.add(new RefreshMenuItem());
		
		super.makeMenu(menu, instance);
	}

	public void setInterstitial(boolean interstitial)
	{
		this.interstitial = interstitial;
	}
	
	public void setSite(int site)
	{
		this.site = site;
	}
	
	public void setZone(int zone)
	{
		this.zone = zone;
	}
	
	protected void onUiEngineAttached(boolean attached)
	{
		if (attached)
		{
			if (adView == null)
			{
				adView = new MASTAdView(interstitial);
				adView.setLogLevel(MASTAdView.LOG_LEVEL_DEBUG);
				
				Background background = BackgroundFactory.createSolidBackground(Color.GRAY);
				adView.setBackground(background);
				
				if (interstitial == false)
				{
					adView.setAdHeight(50);
					super.insert(adView, 0);
				}
				
				adView.setHandler(this);
				adView.setSite(site);
				adView.setZone(zone);
			}
			
			// Update (force) whenever the screen is attached.
			adView.update(0, true);
		}
		else
		{
			if (adView != null)
			{
				// Stop everything when the screen is detached.
				adView.reset();
			}
		}
	}
	
	protected class RefreshMenuItem extends MenuItem
	{
		public RefreshMenuItem()
		{
			super("Refresh", 10, 0);
		}

		public void run()
		{
			if (adView == null)
				return;
			
			RefreshDialog dialog = new RefreshDialog();
			dialog.setSite(adView.getSite());
			dialog.setZone(adView.getZone());
			
			int response = dialog.doModal();
			if (response == Dialog.OK)
			{
				adView.setSite(dialog.getSite());
				adView.setZone(dialog.getZone());
				adView.update();
			}
		}
	}

	public void onAdReceived(MASTAdView sender)
	{
		if (interstitial)
			adView.showInterstitial();
	}

	public void onDownloadError(MASTAdView sender, String message, Exception error)
	{
		
	}

	public boolean onAdClicked(MASTAdView sender, String url)
	{
		return true;
	}

	public void onInternalBrowserOpen(MASTAdView sender)
	{
		
	}

	public void onInternalBrowserClose(MASTAdView sender)
	{
		
	}

	public void onThirdPartyEvent(MASTAdView sender, Hashtable properties, Hashtable params)
	{
		
	}

	public boolean onLogEvent(MASTAdView sender, int type, String message, Exception exception)
	{
		return true;
	}
}
