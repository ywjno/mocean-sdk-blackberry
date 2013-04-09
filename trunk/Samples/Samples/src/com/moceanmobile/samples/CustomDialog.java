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
