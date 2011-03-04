package com.adserver.core;

import net.rim.device.api.ui.UiApplication;
/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public final class AdserverInterstitial extends Adserver {
	
	public static final int CLOSEBUTTONPOSITIONCENTER = 0;
	public static final int CLOSEBUTTONPOSITIONTOP = 1;
	public static final int CLOSEBUTTONPOSITIONBOTTOM = 2;
	public static final int CLOSEBUTTONPOSITIONLEFT = 3;
	public static final int CLOSEBUTTONPOSITIONRIGHT = 4;
	
	private int closeButtonPosition = 0;
	private int showCloseButtonTime = 0;
	int autoCloseInterstitialTime = 0;

	/**
	 * @param appID
	 *            Identificator of application
	 * @param mode
	 *            Working mode:<br>
	 *            MODE_COUNTER_ONLY - no ads, just counter,<br>
	 *            MODE_ADS_ONLY = only ads, no counter,<br>
	 *            MODE_COUNTER_AND_ADS = ads and counter.
	 * @param site
	 *            The id of the publisher site (default: DEFAULT_SITE).
	 * @param zone
	 *            The id of the zone of publisher site (default: DEFAULT_ZONE).
	 * @param ip
	 *            The IP address of the carrier gateway over which the device is connecting (default: DEFAULT_IP).
	 * @param keywords
	 *            Keywords to search ad delimited by commas.
	 * @param adsType
	 *            Type of advertisement (ADS_TYPE_TEXT_ONLY - text only, ADS_TYPE_IMAGES_ONLY - image only,
	 *            ADS_TYPE_TEXT_AND_IMAGES - image and text, ADS_TYPE_SMS - SMS ad, default - DEFAULT_ADS_TYPE).<br>
	 *            SMS will be returned in XML.
	 * @param over18
	 *            Filter by ad over 18 content:<br>
	 *            OVER_18_TYPE_DENY - deny over 18 content,<br>
	 *            OVER_18_TYPE_ONLY - only over 18 content,<br>
	 *            OVER_18_TYPE_ALL - allow all ads including over 18 content). default: DEFAULT_OVER_18
	 * @param latitude
	 * @param longitude
	 * @param ua
	 *            The browser user agent of the device making the request.
	 * @param premium
	 *            Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, PREMIUM_STATUS_PREMIUM - premium only,
	 *            PREMIUM_STATUS_BOTH - both).<br>
	 *            Can be used only by premium publishers.
	 * @param testMode
	 *            Default setting is test mode where, if the ad code is properly installed, the ad response is "Test MODE"
	 *            (default: DEFAULT_TEST_MODE).
	 * @param count
	 *            Quantity of ads, returned by a server (Maximum: 5; Default: DEFAULT_COUNT).
	 * @param country
	 *            Country of visitor. Will override country detected by IP. (http://www.mojiva.com/docs/iso3166.csv)
	 * @param region
	 *            Region of visitor. Codes for US and Canada - http://www.mojiva.com/docs/iso3166_2.csv, others -
	 *            http://www.mojiva.com/docs/fips10_4.csv.
	 * @param textBordersEnabled
	 *            Show borders around text ads (Boolean.FALSE - non-borders, Boolean.TRUE - show borders, default:
	 *            DEFAULT_TEXT_BORDER).
	 * @param paramBorder
	 *            Borders color (#XXXXXX)
	 * @param paramBG
	 *            Background color (#XXXXXX)
	 * @param paramLINK
	 *            Link color (#XXXXXX)
	 * @param carrier
	 *            Carrier name
	 * @param imageSize
	 *            Override size detection for banners (IMAGE_SIZE_SMALLEST - the smallest, IMAGE_SIZE_LARGEST - the largest).
	 * @param target
	 *            Target attribute for:<br>
	 *            TARGET_BLANK - open the linked document in a new window,<br>
	 *            TARGET_SELF - open the linked document in the same frame,<br>
	 *            TARGET_PARENT - open the linked document in the parent frameset,<br>
	 *            TARGET_TOP - open the linked document in the full body of the window)<br>
	 * @param url
	 *            URL of site for which it is necessary to receive advertising.
	 * @param hashId
	 *            Unique id of Adserver instance (used for cache)
	 * @param defaultImage
	 *            Image resource name, that placed on screen when no network and cache
	 * @param adInternalBrowserEnabled
	 *            Open ad links in external or internal browser (default: DEFAULT_AD_BROWSER_MODE)
	 * @param reloadPeriod
	 *            Ad reload period
	 */
	
	public AdserverInterstitial(String campaign, String appID, int mode, String site, String zone, String ip, String keywords,
			Integer adsType, Integer over18, String latitude, String longitude, String ua, Integer premium,
			Boolean testMode, Integer count, String country, String region, Boolean textBordersEnabled, String paramBorder,
			String paramBG, String paramLINK, String carrier, Integer imageSize, String target, String url,
			String hashId, String defaultImage, Boolean adInternalBrowserEnabled,
			AdClickListener clickListener, Integer reloadPeriod, int closeButtonPosition, 
			boolean shiftLayout, int showCloseButtonTime, int autoCloseInterstitialTime, String customParameters) {
		super(campaign, appID, mode, site, zone, ip, keywords, adsType, over18,
				latitude, longitude, ua, premium, testMode, count, country, region,
				textBordersEnabled, paramBorder, paramBG, paramLINK, carrier, imageSize, target, url, hashId,
				defaultImage, adInternalBrowserEnabled, clickListener, reloadPeriod, customParameters);
		
		this.closeButtonPosition = closeButtonPosition;
		this.showCloseButtonTime = showCloseButtonTime;
		this.autoCloseInterstitialTime = autoCloseInterstitialTime;
		
		UiApplication.getUiApplication().pushScreen(new WebViewInterstitial(closeButtonPosition, true, showCloseButtonTime, autoCloseInterstitialTime));
	}
}
