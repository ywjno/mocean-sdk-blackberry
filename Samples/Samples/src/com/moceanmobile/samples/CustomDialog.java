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

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.DialogFieldManager;

public class CustomDialog extends Dialog
{
	private EditField widthField = null;
	private EditField heightField = null;
	private CheckboxField internalBrowserField = null;
	
	public CustomDialog()
	{
		super("Custom", new String[] {"OK", "Cancel"}, new int[] {Dialog.OK, Dialog.CANCEL}, Dialog.OK, null);

		widthField = new EditField("Width: ", "", 10, EditField.EDITABLE | EditField.FILTER_NUMERIC);
		heightField = new EditField("Height: ", "", 10, EditField.EDITABLE | EditField.FILTER_NUMERIC);
		internalBrowserField = new CheckboxField("Use internal browser", false);
		
		Manager delegate = getDelegate();
		if (delegate instanceof DialogFieldManager)
		{
			DialogFieldManager dialogFieldManager = (DialogFieldManager) delegate;
			dialogFieldManager.addCustomField(widthField);
			dialogFieldManager.addCustomField(heightField);
			dialogFieldManager.addCustomField(internalBrowserField);
		}
	}
	
	public void setAdWidth(int width)
	{
		widthField.setText(String.valueOf(width));
	}
	
	public int getAdWidth()
	{
		return Integer.parseInt(widthField.getText());
	}
	
	public void setAdHeight(int height)
	{
		heightField.setText(String.valueOf(height));
	}
	
	public int getAdHeight()
	{
		return Integer.parseInt(heightField.getText());
	}
	
	public void setUseInternalBrowser(boolean use)
	{
		internalBrowserField.setChecked(use);
	}
	
	public boolean getUseInternalBrowser()
	{
		return internalBrowserField.getChecked();
	}
}
