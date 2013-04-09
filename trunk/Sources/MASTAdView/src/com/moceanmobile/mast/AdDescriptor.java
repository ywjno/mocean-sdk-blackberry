package com.moceanmobile.mast;

import java.util.Hashtable;

public class AdDescriptor
{
	private Hashtable info = new Hashtable(6);
	
	public AdDescriptor()
	{

	}
	
	public void setInfo(String key, String value)
	{
		info.put(key, value);
	}

	public String getType() 
	{
		return (String) info.get("type");
	}

	public String getUrl() 
	{
		return (String) info.get("url");
	}

	public String getText()
	{
		return (String) info.get("text");
	}

	public String getImg() 
	{
		return (String) info.get("img");
	}

	public String getContent()
	{
		return (String) info.get("content");
	}

	public String getTrack()
	{
		return (String) info.get("track");
	}
}
