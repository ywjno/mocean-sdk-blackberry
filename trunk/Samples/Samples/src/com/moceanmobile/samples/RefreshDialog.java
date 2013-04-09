package com.moceanmobile.samples;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.DialogFieldManager;


public class RefreshDialog extends Dialog
{
	private EditField siteField = null;
	private EditField zoneField = null;
	
	public RefreshDialog()
	{
		super("Refresh", new String[] {"OK", "Cancel"}, new int[] {Dialog.OK, Dialog.CANCEL}, Dialog.OK, null);
		
		siteField = new EditField("Site: ", "", 10, EditField.EDITABLE | EditField.FILTER_NUMERIC);
		zoneField = new EditField("Zone: ", "", 10, EditField.EDITABLE | EditField.FILTER_NUMERIC);
		
		Manager delegate = getDelegate();
		if (delegate instanceof DialogFieldManager)
		{
			DialogFieldManager dialogFieldManager = (DialogFieldManager) delegate;
			dialogFieldManager.addCustomField(siteField);
			dialogFieldManager.addCustomField(zoneField);
		}
	}
	
	public void setSite(int site)
	{
		siteField.setText(String.valueOf(site));
	}
	
	public int getSite()
	{
		return Integer.parseInt(siteField.getText());
	}
	
	public void setZone(int zone)
	{
		zoneField.setText(String.valueOf(zone));
	}
	
	public int getZone()
	{
		return Integer.parseInt(zoneField.getText());
	}
}
