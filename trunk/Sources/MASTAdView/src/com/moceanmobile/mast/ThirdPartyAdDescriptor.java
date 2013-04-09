package com.moceanmobile.mast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import net.rim.device.api.xml.jaxp.XMLParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ThirdPartyAdDescriptor
{
	private Hashtable properties = new Hashtable();
	private Hashtable params = new Hashtable();
	
	public ThirdPartyAdDescriptor()
	{

	}
	
	public void parse(String clientSideExternalCampaignContent) throws SAXException, IOException
	{
		properties.clear();
		params.clear();
		
		String startString = "<external_campaign";
		String endString = "</external_campaign>";
		
		int start = clientSideExternalCampaignContent.indexOf(startString);
		if (start < 0)
			throw new IllegalArgumentException("clientSideExternalCampaignContent missing " + startString);
		
		int end = clientSideExternalCampaignContent.indexOf(endString, start);
		if (end < 0)
			throw new IllegalArgumentException("clientSideExternalCampaignContent missing " + endString);
		
		String content = clientSideExternalCampaignContent.substring(start, end + endString.length());
		
		byte[] buffer = content.getBytes("UTF-8");
		InputStream inputStream = new ByteArrayInputStream(buffer);

		XMLParser xmlParser = new XMLParser();
		xmlParser.parse(inputStream, new Handler());
	}
	
	public Hashtable getProperties()
	{
		return properties;
	}
	
	public Hashtable getParams()
	{
		return params;
	}
	
	private class Handler extends DefaultHandler
	{
		private Attributes elementAttributes = null;
		private String content = null;
		
		public Handler()
		{
			
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			elementAttributes = attributes;
			content = "";
		}
		
		public void endElement(String uri, String localName, String qName)
		{
			if (content == null)
				return;
			
			if ("param".equals(localName))
			{
				String key = elementAttributes.getValue("", "name");

				if (key != null)
					params.put(key, content);
			}
			else
			{
				properties.put(localName, content);
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
