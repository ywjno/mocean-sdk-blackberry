package com.adserver.core;

import java.util.Hashtable;

import com.adserver.utils.EventListener;

import net.rim.device.api.ui.UiApplication;
/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public final class AdserverInterstitial {
	
	public static final int CLOSEBUTTONPOSITIONCENTER = 0;
	public static final int CLOSEBUTTONPOSITIONTOP = 1;
	public static final int CLOSEBUTTONPOSITIONBOTTOM = 2;
	public static final int CLOSEBUTTONPOSITIONLEFT = 3;
	public static final int CLOSEBUTTONPOSITIONRIGHT = 4;
	
	private int closeButtonPosition = 0;
	private int showCloseButtonTime = 0;
	private int autoCloseInterstitialTime = 0;
	protected AdClickListener clickListener;

	
	Adserver adserver;
	/**
	 * @param site
	 *            The id of the publisher site (default: DEFAULT_SITE).
	 * @param zone
	 *            The id of the zone of publisher site (default: DEFAULT_ZONE).
	 */

	public AdserverInterstitial(int site, int zone) {
		adserver = new Adserver (site, zone);
//		UiApplication.getUiApplication().pushScreen(new WebViewInterstitial(adserver, this));
		}

	/**
	 * Push AdserverInterstitial to display stack. 
	 */
	public void pushScreen() {
		UiApplication.getUiApplication().pushScreen(new WebViewInterstitial(adserver, this));
	}

	// /////////////////////////////////////////////////////////////////
	// Getters - setters
	/**
	 * Get close button position. 
	 */
	public int getCloseButtonPosition() {
		return closeButtonPosition;
	}

	/**
	 * Set close button position. 
	 */
	public void setCloseButtonPosition(int closeButtonPosition) {
		this.closeButtonPosition = closeButtonPosition;
	}

	/**
	 * Get close button delay time. 
	 */
	public int getShowCloseButtonTime() {
		return showCloseButtonTime;
	}

	/**
	 * Set close button delay time. 
	 */
	public void setShowCloseButtonTime(int showCloseButtonTime) {
		this.showCloseButtonTime = showCloseButtonTime;
	}

	/**
	 * Get interstitial control close time. 
	 */
	public int getAutoCloseInterstitialTime() {
		return autoCloseInterstitialTime;
	}

	/**
	 * Set interstitial control close time. 
	 */
	public void setAutoCloseInterstitialTime(int autoCloseInterstitialTime) {
		this.autoCloseInterstitialTime = autoCloseInterstitialTime;
	}
	/**
	 * Required.
	 * Set the id of the publisher site. 
	 */
	public void setSite(int site) {
		adserver.setSite(site);
	}

	/**
	 * Get the id of the publisher site. 
	 */
	public int getSite() {
		return adserver.getSite();
	}
	/**
	 * Required.
	 * Set the id of the zone of publisher site.
	 * @param zone
	 */
	public void setZone(int zone) {
		adserver.setZone(zone);
	}
	
	/**
	 * Get the id of the zone of publisher site.
	 * @param zone
	 */
	public int getZone() {
		return adserver.getZone();
	}
	
	/**
	 * Optional.
	 * Set Default setting is test mode where, if the ad code is properly installed, 
	 * the ad response is "Test MODE".
	 * @param enabled
	 */
	public void setTest(Boolean enabled) {
		adserver.setTest(enabled);
	}

	/**
	 * Optional.
	 * Get test mode setting.
	 */
	public Boolean getTest() {
		return adserver.getTest();
	}

	/**
	 * Optional.
	 * Set Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, 
	 * PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). 
	 * Can be used only by premium publishers.
	 * @param premium
	 */
	public void setPremium(Integer premium) {
		adserver.setPremium(premium);
	}

	/**
	 * Optional.
	 * Get Filter by premium.
	 */
	public Integer getPremium() {
		return adserver.getPremium();
	}
	
	/**
	 * Optional.
	 * Set Keywords to search ad delimited by commas.
	 * @param keywords
	 */
	public void setKeywords(String keywords) {
		adserver.setKeywords(keywords);
	}
	
	/**
	 * Optional.
	 * Get Keywords to search ad delimited by commas.
	 */
	public String getKeywords() {
		return adserver.getKeywords();
	}
	
	/**
	 * Optional.
	 * Set minimum width of advertising. 
	 * @param minSizeX
	 */
	public void setMinSizeX(Integer minSizeX) {
		adserver.setMinSizeX(minSizeX);
	}
	
	/**
	 * Optional.
	 * Get minimum width of advertising. 
	 */
	public Integer getMinSizeX() {
		return adserver.getMinSizeX();
	}
	
	/**
	 * Optional.
	 * Set minimum height of advertising. 
	 * @param minSizeY
	 */
	public void setMinSizeY(Integer minSizeY) {
		adserver.setMinSizeY(minSizeY);
	}
	
	/**
	 * Optional.
	 * Get minimum height of advertising. 
	 */
	public Integer getMinSizeY() {
		return adserver.getMinSizeY();
	}
	
	/**
	 * Optional.
	 * Set maximum width of advertising. 
	 * @param maxSizeX
	 */
	public void setMaxSizeX(Integer maxSizeX) {
		adserver.setMaxSizeX(maxSizeX);
	}
	
	/**
	 * Optional.
	 * Get maximum width of advertising. 
	 */
	public Integer getMaxSizeX() {
		return adserver.getMaxSizeX();
	}
	
	/**
	 * Optional.
	 * Set maximum height of advertising. 
	 * @param maxSizeY
	 */
	public void setMaxSizeY(Integer maxSizeY) {
		adserver.setMaxSizeY(maxSizeY);
	}
	
	/**
	 * Optional.
	 * Get maximum height of advertising. 
	 */
	public Integer getMaxSizeY() {
		return adserver.getMaxSizeY();
	}
	
	/**
	 * Optional.
	 * Set Background color of advertising in HEX.
	 * @param backgroundColor
	 */
	public void setBackgroundColor(String backgroundColor) {
		adserver.setBackgroundColor(backgroundColor);
	}

	/**
	 * Optional.
	 * Get Background color of advertising in HEX.
	 */
	public String getBackgroundColor() {
		return adserver.getBackgroundColor();
	}

	/**
	 * Optional.
	 * Set Text color of links in HEX.
	 * @param textColor
	 */
	public void setTextColor(String textColor) {
		adserver.setTextColor(textColor);
	}
	
	/**
	 * Optional.
	 * Get Text color of links in HEX.
	 */
	public String getTextColor() {
		return adserver.getTextColor();
	}

	/**
	 * Optional.
	 * Overrides the URL of ad server.
	 * @param adserverURL
	 */
	public void setAdServerUrl(String adserverUrl) {
		adserver.setAdServerUrl(adserverUrl);
	}

	/**
	 * Optional.
	 * Get URL of ad server.
	 */
	public String getAdServerUrl() {
		return adserver.getAdServerUrl();
	}

	/**
	 * Optional.
	 * Set user location latitude value (given in degrees.decimal degrees).
	 * @param latitude
	 */
	public void setLatitude(String latitude) {
		adserver.setLatitude(latitude);
	}
	
	/**
	 * Optional.
	 * Get user location latitude value (given in degrees.decimal degrees).
	 */
	public String getLatitude() {
		return adserver.getLatitude();
	}
	
	/**
	 * Optional.
	 * Set user location longitude value (given in degrees.decimal degrees).
	 * @param longitude
	 */
	public void setLongitude(String longitude) {
			adserver.setLongitude(longitude);
	}
	
	/**
	 * Optional.
	 * Get user location longitude value (given in degrees.decimal degrees).
	 */
	public String getLongitude() {
			return adserver.getLongitude();
	}
	
	/**
	 * Optional.
	 * Set Country of visitor. 
	 * @param country
	 */
	public void setCountry(String country) {
			adserver.setCountry(country);
	}
	
	/**
	 * Optional.
	 * Get Country of visitor.
	 */
	public String getCountry() {
		return adserver.getCountry();
	}

	/**
	 * Optional.
	 * Set Region of visitor. 
	 * @param region
	 */
	public void setRegion(String region) {
			adserver.setRegion(region);
	}
	
	/**
	 * Optional.
	 * Get Region of visitor.
	 */
	public String getRegion() {
		return adserver.getRegion();
	}

	/**
	 * Optional.
	 * Set City of the device user (with state). For US only. 
	 * @param city
	 */
	public void setCity(String city) {
		adserver.setCity(city);
	}

	/**
	 * Optional.
	 * Get City of the device user (with state). For US only. 
	 */
	public String getCity() {
		return adserver.getCity();
	}
	
	/**
	 * Optional.
	 * Set Area code of a user. For US only. 
	 * @param area
	 */
	public void setArea(String area) {
		adserver.setArea(area);
	}

	/**
	 * Optional.
	 * Get Area code of a user. For US only. 
	 */
	public String getArea() {
		return adserver.getArea();
	}
	
	/**
	 * Optional.
	 * Set Metro code of a user. For US only. 
	 * @param metro
	 */
	public void setMetro(String metro) {
		adserver.setMetro(metro);
	}
	
	/**
	 * Optional.
	 * Get Metro code of a user. For US only. 
	 */
	public String getMetro() {
		return adserver.getMetro();
	}
	
	/**
	 * Optional.
	 * Set Zip/Postal code of user. For US only. 
	 * @param zip
	 */
	public void setZip(String zip) {
		adserver.setZip(zip);
	}
	
	/**
	 * Optional.
	 * Get Zip/Postal code of user. For US only. 
	 */
	public String getZip() {
		return adserver.getZip();
	}
	
	/**
	 * Optional.
	 * Set User carrier.
	 * @param carrier
	 */
	public void setCarrier(String carrier) {
		adserver.setCarrier(carrier);
	}
	
	/**
	 * Optional.
	 * Get User carrier.
	 */
	public String getCarrier() {
		return adserver.getCarrier();
	}

	
	/**
	 * Optional.
	 * Get Custom Parameters.
	 * @return Hashtable customParameters
	 */
	public Hashtable getCustomParameters() {
		if(adserver != null) {
			return adserver.getCustomParameters();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set Custom Parameters.
	 * @param customParameters
	 */
	public void setCustomParameters(Hashtable customParameters) {
		if(adserver != null) {
			adserver.setCustomParameters(customParameters);
		}
	}


	/**
	 * Optional.
	 * Set banner refresh interval (in seconds).
	 */
	public void setUpdateTime(int reloadPeriod) {
		adserver.setUpdateTime(reloadPeriod);
	}
	
	/**
	 * Set the flag which operates advertising opening.
	 * @param internalBrowser
	 */
	public void setInternalBrowser (boolean internalBrowser) {
		adserver.adInternalBrowserEnabled = internalBrowser;
	}
	
	/**
	 * Optional.
	 * Set image resource which will be shown during advertising loading if there is no advertising in a cache.
	 */
	public void setDefaultImage (String defaultImage) {
		adserver.defaultImage = defaultImage;
	}
	/**
	 * Optional.
	 * Get Advertiser id (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public String getAdvertiserId() {
		return adserver.getAdvertiserId();
	}
	
	/**
	 * Optional.
	 * Set Advertiser id (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public void setAdvertiserId(String advertiserId) {
		adserver.setAdvertiserId(advertiserId);
	}

	/**
	 * Optional.
	 * Get Group code (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public String getGroupCode() {
		return adserver.getGroupCode();
	}
	
	/**
	 * Optional.
	 * Set Group code (if both AdvertiserId and GroupCode are specified then install notification is enabled).
	 */
	public void setGroupCode(String groupCode) {
		adserver.setGroupCode(groupCode);
	}
	

	/**
	 * Optional.
	 * Set Ad size in pixels
	 */
	public void setSize(int width, int height) {
		adserver.setSize(width, height);
		
	}

	/**
	 * Set form click listener.
	 * @param listener
	 */
	public void setClickListener(AdClickListener listener) {
		adserver.clickListener = listener;
	}
	// from Observable.java class
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Set form event listener.
	 * @param listener
	 */
	public void addListener(EventListener listener) {
		adserver.addListener(listener);
	}

}
