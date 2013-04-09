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
