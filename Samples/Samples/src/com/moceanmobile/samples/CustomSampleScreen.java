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
