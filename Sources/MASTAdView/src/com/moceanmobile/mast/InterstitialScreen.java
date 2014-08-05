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

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.container.FullScreen;

public class InterstitialScreen extends FullScreen
{
	private final Handler handler;
	
	private int duration;
	private Timer durationTimer = null;
	
	private boolean allowClose = false;
	private int delayCloseDuration;
	private Timer delayCloseTimer = null;

	public InterstitialScreen(Handler handler)
	{
		/** Not using a menu since it would require localized strings.
		super(FullScreen.DEFAULT_MENU | FullScreen.NO_SYSTEM_MENU_ITEMS);
		**/
		
		this.handler = handler;
	}
	
	// Call to reset the timers.  The timers however will not start
	// unless the form is visible or after the form becomes visible.
	public void setTimers(int duration, int delayCloseDuration)
	{
		if (durationTimer != null)
		{
			durationTimer.cancel();
			durationTimer = null;
		}
		
		if (delayCloseTimer != null)
		{
			delayCloseTimer.cancel();
			delayCloseTimer = null;
		}
		
		this.duration = duration;
		this.delayCloseDuration = delayCloseDuration;
		
		if (this.delayCloseDuration < 1)
			allowClose = true;
		
		if (this.isVisible())
			activateTimers();
	}
	
	// Called when the screen is made visible.
	private void activateTimers()
	{
		// This timer will close the view automatically after
		// the delay once made visible.
		if ((duration > 0) && (durationTimer == null))
		{
			durationTimer = new Timer();
			durationTimer.schedule(new TimerTask()
			{
				public void run()
				{
					if (handler != null)
					{
						handler.onInterstitialClose(InterstitialScreen.this);
					}
				}
			}, duration * 1000);
		}
		
		// This timer delays the close action once made visible.
		if (allowClose == false)
		{
			delayCloseTimer = new Timer();
			delayCloseTimer.schedule(new TimerTask()
			{
				public void run()
				{
					allowClose = true;
				}
			}, delayCloseDuration * 1000);
		}		
	}
	
	// Called to cancel the timers so that user interaction cancles any automated behavior.
	public void cancelTimers()
	{
		if (durationTimer == null)
		{
			durationTimer.cancel();
		}
		
		if (delayCloseTimer != null)
		{
			delayCloseTimer.cancel();
		}
		
		allowClose = true;
	}
	
	public boolean onClose()
	{
		if (durationTimer != null)
		{
			durationTimer.cancel();
			durationTimer = null;
		}
		
		if (delayCloseTimer != null)
		{
			delayCloseTimer.cancel();
			delayCloseTimer = null;
		}
		
		return super.onClose();
	}
	
	/** Not using a menu since it would require localized strings.
	public boolean onMenu(int instance)
	{
		return super.onMenu(instance);
	}
	
	protected void makeMenu(Menu menu, int instance)
	{
		menu.add(new OpenMenuItem());
		
		if (allowClose)
			menu.add(new CloseMenuItem());
		
		super.makeMenu(menu, instance);
	}
	**/
	
	protected void onVisibilityChange(boolean visible)
	{
		super.onVisibilityChange(visible);
		
		if (visible)
		{
			activateTimers();
			
			if (handler != null)
			{
				handler.onInterstitialVisibile(this);	
			}
		}
	}
	
	protected boolean trackwheelClick(int status, int time)
	{
		if (handler != null)
		{
			handler.onInterstitialClick(this);
			return true;
		}
		
		return super.trackwheelClick(status, time);
	}
	
	protected boolean keyDown(int keycode, int time)
	{
		int key = Keypad.key(keycode);
		if  (key == Characters.ESCAPE)
		{
			if (allowClose && (handler != null))
			{
				handler.onInterstitialClose(this);
			}
			return true;
		}
		
		return super.keyDown(keycode, time);
	}
	
	public interface Handler
	{
		void onInterstitialVisibile(InterstitialScreen sender);
		void onInterstitialClick(InterstitialScreen sender);
		void onInterstitialClose(InterstitialScreen sender);
	}
	
	/** Not using a menu since it would require localized strings.
	private class OpenMenuItem extends MenuItem
	{
		public OpenMenuItem()
		{
			super("Open", 0, 0);
		}
		
		public void run()
		{
			if (handler != null)
			{
				handler.onInterstitialClick(InterstitialScreen.this);
			}
		}		
	}
	
	private class CloseMenuItem extends MenuItem
	{
		public CloseMenuItem()
		{
			super("Close", 0, 0);
		}
		
		public void run()
		{
			if (handler != null)
			{
				handler.onInterstitialClose(InterstitialScreen.this);
			}
		}
	}
	**/
}
