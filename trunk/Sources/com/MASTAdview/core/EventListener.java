package com.MASTAdview.core;

/**
 * Ad click listener
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */

/**
 * The interface for advertising downloading.
 */
public interface EventListener {
	
	/**
	 * This event is fired before banner download begins. 
	 */
	public abstract void onStartLoading();

	/**
	 * This event is fired after banner content fully downloaded. 
	 */
	public abstract void onLoaded();
	
	/**
	 * This event is fired after fail to download content. 
	 */
	public abstract void onError(final String msg);
}
