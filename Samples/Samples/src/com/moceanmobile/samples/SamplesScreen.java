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

import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;

import com.moceanmobile.mast.MASTAdView;

public final class SamplesScreen extends MainScreen
{
    public SamplesScreen()
    {
    	super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
    	
    	setTitle("Samples - MASTAdView " + MASTAdView.getVersion());

    	add(new LabelField("Simple"));
    	ListField listField = new ListField();
    	SamplesList samplesList = new SamplesList();
    	listField.setCallback(samplesList);
    	addSimpleSamples(samplesList);
    	listField.setSize(samplesList.getSize());
    	add(listField);
    	
    	add(new LabelField("Custom"));
    	listField = new ListField();
    	samplesList = new SamplesList();
    	listField.setCallback(samplesList);
    	addCustomSamples(samplesList);
    	listField.setSize(samplesList.getSize());
    	add(listField); 
    	
    	add(new LabelField("Delegate"));
    	listField = new ListField();
    	samplesList = new SamplesList();
    	listField.setCallback(samplesList);
    	addDelegateSamples(samplesList);
    	listField.setSize(samplesList.getSize());
    	add(listField); 
    	
    	add(new LabelField("Error"));
    	listField = new ListField();
    	samplesList = new SamplesList();
    	listField.setCallback(samplesList);
    	addErrorSamples(samplesList);
    	listField.setSize(samplesList.getSize());
    	add(listField); 
    }
    
    private void addSimpleSamples(SamplesList list)
    {
    	SampleItem sample = new SampleItem("Image", SampleScreen.class, false, 19829, 88269);
    	list.add(sample);
    	
    	sample = new SampleItem("Animated GIF", SampleScreen.class, false, 19829, 146951);
    	list.add(sample);
    	
    	sample = new SampleItem("Interstitial", SampleScreen.class, true, 19829, 88269);
    	list.add(sample);
    	
    	sample = new SampleItem("HTML", SampleScreen.class, false, 19829, 174865);
    	list.add(sample);
    	
    	sample = new SampleItem("Text", SampleScreen.class, false, 19829, 89888);
    	list.add(sample);
    }
    
    private void addCustomSamples(SamplesList list)
    {
    	SampleItem sample = new SampleItem("Custom Ad Setup", CustomSampleScreen.class, false, 19829, 88269);
    	list.add(sample);
    }
    
    private void addDelegateSamples(SamplesList list)
    {
    	SampleItem sample = new SampleItem("Generic", DelegateSampleScreen.class, false, 19829, 88269);
    	list.add(sample);
    	
    	sample = new SampleItem("Third Party Request", DelegateSampleScreen.class, false, 19829, 90038);
    	list.add(sample);
    	
    	sample = new SampleItem("No Content Zone", DelegateSampleScreen.class, false, 19829, 158514);
    	list.add(sample);
    }
    
    private void addErrorSamples(SamplesList list)
    {
    	SampleItem sample = new SampleItem("Hide", ErrorHideSampleScreen.class, false, 19829, 88269);
    	list.add(sample);
    	
    	sample = new SampleItem("Image", ErrorImageSampleScreen.class, false, 19829, 88269);
    	list.add(sample);
    	
    	sample = new SampleItem("Reset", ErrorResetSampleScreen.class, false, 19829, 88269);
    	list.add(sample);
    }
    
    protected boolean trackwheelClick(int status, int time)
    {
    	Field field = getLeafFieldWithFocus();
    	if (field instanceof ListField)
    	{
    		ListField listField = (ListField) field;
    		Object callback = listField.getCallback();
    		
    		if (callback instanceof SamplesList)
    		{
    			SamplesList samplesList = (SamplesList) callback;
    			SampleItem sampleItem = samplesList.sampleItemAtIndex(listField.getSelectedIndex());
    			
    			if (sampleItem != null)
    			{
        			SampleScreen screen = sampleItem.createScreen();
        			UiApplication.getUiApplication().pushScreen(screen);
        			
        			return true;
    			}
    		}
    	}
    	
    	return super.trackwheelClick(status, time);
    }
    
    private class SampleItem 
    {
    	private final String title;
    	private final Class screenClass;
    	private final boolean interstitial;
    	private final int site;
    	private final int zone;
    	
    	public SampleItem(String title, Class screenClass, boolean interstitial, int site, int zone)
    	{
    		this.title = title;
    		this.screenClass = screenClass;
    		this.interstitial = interstitial;
    		this.site = site;
    		this.zone = zone;
    	}
    	
    	public String getTitle()
    	{
    		return title;
    	}
    	
    	public SampleScreen createScreen()
    	{
    		try
    		{
    			SampleScreen screen = (SampleScreen) screenClass.newInstance();
    			screen.setTitle(title);
    			screen.setInterstitial(interstitial);
    			screen.setSite(site);
    			screen.setZone(zone);
    			return screen;
    		}
    		catch (Exception ex)
    		{
    			return null;
    		}
    	}
    }
    
    private class SamplesList implements ListFieldCallback
    {
    	private Vector list = new Vector();
    	private Hashtable map = new Hashtable();
    	
    	public void add(SampleItem sampleItem)
    	{
        	list.addElement(sampleItem.getTitle());
        	map.put(new Integer(list.size() - 1), sampleItem);
    	}
    	
    	public int getSize()
    	{
    		return list.size();
    	}
    	
    	public SampleItem sampleItemAtIndex(int index)
    	{
    		SampleItem sampleItem = (SampleItem) map.get(new Integer(index));
    		return sampleItem;
    	}
    	
		public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width)
		{
			String text = (String) list.elementAt(index);
			graphics.drawText(text, 20, y, 0, width);
		}

		public Object get(ListField listField, int index)
		{
			return list.elementAt(index);
		}

		public int getPreferredWidth(ListField listField)
		{
			return Display.getWidth();
		}

		public int indexOfList(ListField listField, String prefix, int start)
		{
			return -1;
		}
    }
}
