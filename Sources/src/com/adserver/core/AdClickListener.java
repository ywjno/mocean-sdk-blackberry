package com.adserver.core;

/**
 * Ad click listener <br>
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public interface AdClickListener {

	/**
	 * Invoked when ad link clicked
	 * and only when link opened in Internal Browser
	 * 
	 * @param url
	 *            clicked ads link url
	 * @return
	 * 			  return true if you want to handle link by you self
	 * 			  return false if you want to open link in internal browser           
	 */
	public abstract boolean didAdClicked(String url);
}
