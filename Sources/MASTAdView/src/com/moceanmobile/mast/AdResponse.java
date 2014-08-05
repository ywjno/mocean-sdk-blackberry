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

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import net.rim.device.api.xml.jaxp.XMLParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AdResponse
{
	private String errorCode = null;
	private String errorMessage = null;
	private Vector adDescriptors = new Vector();
	
	public AdResponse()
	{
		
	}
	
	public Vector getAdDescriptors()
	{
		return adDescriptors;
	}
	
	public String getErrorCode()
	{
		return errorCode;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public void parse(InputStream inputStream) throws SAXException, IOException
	{	
		XMLParser xmlParser = new XMLParser();
		xmlParser.parse(inputStream, new AdResponseHandler());
	}
	
	private class AdResponseHandler extends DefaultHandler
	{
		private String content = null;
		private AdDescriptor adDescriptor = null;
		
		public AdResponseHandler()
		{
			
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			content = "";
			
			if (adDescriptor != null)
			{
				// Parsing the ad so continue on.
				return;
			}
			
			if ("ad".equals(localName))
			{
				adDescriptor = new AdDescriptor();
				
				if (attributes != null)
					adDescriptor.setInfo("type", attributes.getValue(uri, "type"));
			}
			else if ("error".equals(localName))
			{
				errorCode = attributes.getValue(uri, "code");
			}
		}
		
		public void endElement(String uri, String localName, String qName)
		{
			if (adDescriptor != null)
			{
				if ("ad".equals(localName))
				{
					adDescriptors.addElement(adDescriptor);
					adDescriptor = null;
				}
				else
				{
					adDescriptor.setInfo(localName, content);
					return;
				}
			}
			
			if ((errorCode != null) && "error".equals(localName))
			{
				errorMessage = content;
			}
			
			content = null;
		}
		
		public void characters(char[] ch, int start, int length)
		{
			if (content != null)
				content += String.valueOf(ch, start, length);
		}
	}
}
