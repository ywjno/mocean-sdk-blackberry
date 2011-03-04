package com.adserver.core;

import com.adserver.utils.Constants;
import com.adserver.utils.URLParamEncoder;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class AdserverRequest {


	/**
	 * Filter by non-premium
	 */
	public final static int			PREMIUM_STATUS_NON_PREMIUM		= 0;

	/**
	 * Filter by premium only
	 */
	public final static int			PREMIUM_STATUS_PREMIUM			= 1;

	/**
	 * Filter by premium both
	 */
	public final static int			PREMIUM_STATUS_BOTH				= 2;

	/**
	 * Normal output format
	 */
	public final static int			OUTPUT_FORMAT_NORMAL			= 1;

	/**
	 * XML output format
	 */
	public final static int			OUTPUT_FORMAT_XML				= 3;

	/**
	 * JSON output format
	 */
	public final static int			OUTPUT_FORMAT_JSON				= 5;

	/**
	 * Text advertisement
	 */
	public final static int			ADS_TYPE_TEXT_ONLY				= 1;

	/**
	 * Image advertisement
	 */
	public final static int			ADS_TYPE_IMAGES_ONLY			= 2;

	/**
	 * Text and image advertisement
	 */
	public final static int			ADS_TYPE_TEXT_AND_IMAGES		= 3;

	/**
	 * SMS advertisement
	 */
	public final static int			ADS_TYPE_SMS					= 3;

	/**
	 * Filter by ad: deny only over 18 content
	 */
	public final static int			OVER_18_TYPE_ONLY				= 2;

	/**
	 * Not filter ad
	 */
	public final static int			OVER_18_TYPE_ALL				= 3;

	/**
	 * Smallest banner
	 */
	public final static int			IMAGE_SIZE_SMALLEST				= 1;

	/**
	 * Largest banner
	 */
	public final static int			IMAGE_SIZE_LARGEST				= 4;

	/**
	 * Open the linked document in a new window
	 */
	public final static String		TARGET_BLANK					= "_blank";

	/**
	 * Open the linked document in the same frame
	 */
	public final static String		TARGET_SELF						= "_self";

	/**
	 * Open the linked document in the parent frameset
	 */
	public final static String		TARGET_PARENT					= "_parent";

	/**
	 * Open the linked document in the full body of the window
	 */
	public final static String		TARGET_TOP						= "_top";

	/**
	 * Event occur when downloaded ads and initialized browser
	 */
	public final static int			EVENT_BROWSER_FIELD				= 1;

	/**
	 * Event occur when no network on mobile device
	 */
	public final static int			EVENT_NO_NETWORK				= 2;

	/**
	 * Event occur when the remote server is not responding
	 */
	public final static int			EVENT_ERROR_WITH_REMOTE_SERVER	= 3;

	/**
	 * Default arguments
	 */
	private final static String		DEFAULT_SITE					= "5441";
	private final static String		DEFAULT_ZONE					= "6365";
	private final static Integer	DEFAULT_PREMIUM					= new Integer(PREMIUM_STATUS_NON_PREMIUM);
	private final static Boolean	DEFAULT_TEST_MODE				= Boolean.FALSE;
	private final static Boolean	DEFAULT_TEXT_BORDER				= Boolean.FALSE;
	private final static Integer	DEFAULT_IMAGE_SIZE				= new Integer(1);
	private final static String		DEFAULT_URL						= "http://ads.moblin.com/ad";
	private final static Boolean	DEFAULT_PIXEL_MODE				= Boolean.FALSE;


//  Required	
	/**
	 * The id of the publisher site (default: DEFAULT_SITE).
	 */
	public static String					site							= null;

	/**
	 * The id of the zone of publisher site (default: DEFAULT_ZONE).
	 */
	public static String					zone							= null;

//	Required (autedetect)
	/**
	 * The browser user agent of the device making the request.
	 */
	public static String					ua								= null;

//  Optional
	/**
	 * Default setting is test mode where, if the ad code is properly installed, the ad response is "Test MODE" (default:
	 * DEFAULT_TEST_MODE).
	 */
	public static String					testMode						= null;

	/**
	 * Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH -
	 * both).<br>
	 * Can be used only by premium publishers.
	 */
	public static Integer					premium							= null;

	/**
	 * Keywords to search ad delimited by commas.
	 */
	public static String					keywords						= null;

	/**
	 * Minimal width of the ad banner to be shown
	 */
	public static Integer					minSizeX							= null;

	/**
	 * Minimal height of the ad banner to be shown
	 */
	public static Integer					minSizeY							= null;

	/**
	 * Maximal width of the ad banner to be shown
	 */
	public static Integer					maxSizeX							= null;

	/**
	 * Maximal width of the ad banner to be shown
	 */
	public static Integer					maxSizeY							= null;

	/**
	 * Background color (#XXXXXX)
	 */
	public static String					paramBG							= null;

	/**
	 * Link color (#XXXXXX)
	 */
	public static String					paramLINK						= null;

	/**
	 * CustomParameters comma separated
	 */
	public static String			customParameters				= "";

	/**
	 * Maximal width of the ad banner to be shown
	 */
	public static String adServerUrl = null;


	//GEO Optional
	/**
	 * Country of visitor. Will override country detected by IP. (http://www.mojiva.com/docs/iso3166.csv)
	 */
	public static String					latitude						= null;
	/**
	 * User location longtitude value (given in degrees.decimal degrees). It’s used together with ’lat’ parameter.
	 */
	public static String					longitude						= null;

	/**
	 * Country of visitor. Will override country detected by IP. (http://www.mojiva.com/docs/iso3166.csv)
	 */
	public static String					country							= null;

	/**
	 * Region of visitor. Codes for US and Canada - http://www.mojiva.com/docs/iso3166_2.csv, others -
	 * http://www.mojiva.com/docs/fips10_4.csv.
	 */
	public static String					region							= null;

	/**
	 * City of the device user (with state). For US only.
	 */
	public static String					city							= null;

	/**
	 * Area code of a user. For US only
	 */
	public static String					area							= null;

	/**
	 * Metro code of a user. For US only
	 */
	public static String					metro							= null;

	/**
	 * Zip/Postal code of user (note: parameter is all caps). For US only.
	 */
	public static String					zip							= null;

	/**
	 * User carrier.
	 */
	public static String					carrier							= null;

	/**
	 * 0 - low (gprs, edge), 1 - fast (3g, wifi).
	 */
	public static Integer connectionSpeed 									= null;

	//	Internal
	/**
	 * Set to 1. Output format. Normal format uses key = 1. Parameter key should be set to 3 in order to use XML output and to 5 in order to use JSON output.
	 */
	public static Integer					key								= Constants.KEY;
	
	/**
	 * Set to 3. Type of ads (1 - text only, 2 - image only, 3 - image and text, 6 - SMS ad). SMS will be ONLY returned in XML and should be used along with key=3.
	 */
	public static Integer					adsType							= null;
	
	/**
	 * Set to 1. Count of ads. Default = 1
	 */
	public static Integer					count							= null;

	/**
	 * SDK version
	 */
	public static String					version							= null;
	
	
	
	/**
	 * Constructor
	 * 
	 * @param site
	 *            The id of the publisher site (default: DEFAULT_SITE).
	 * @param zone
	 *            The id of the zone of publisher site (default: DEFAULT_ZONE).
	 * @param keywords
	 *            Keywords to search ad delimited by commas.
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
	 * @param country
	 *            Country of visitor. Will override country detected by IP. (http://www.mojiva.com/docs/iso3166.csv)
	 * @param region
	 *            Region of visitor. Codes for US and Canada - http://www.mojiva.com/docs/iso3166_2.csv, others -
	 *            http://www.mojiva.com/docs/fips10_4.csv.
	 * @param paramBG
	 *            Background color (#XXXXXX)
	 * @param paramLINK
	 *            Link color (#XXXXXX)
	 * @param carrier
	 *            Carrier name
	 * @param url
	 *            URL of site for which it is necessary to receive advertising.
	 */
	public AdserverRequest(String site, String zone, String keywords,
			String latitude, String longitude, String ua, Integer premium, String testMode,
			String country, String region, String paramBG, String paramLINK,
			String carrier, String url, String customParameters) {

		
		AdserverRequest.site = checkStringValue(site, DEFAULT_SITE);
		AdserverRequest.zone = checkStringValue(zone, DEFAULT_ZONE);
		AdserverRequest.ua = ua;
		AdserverRequest.testMode = checkStringValue(testMode, "0");
		AdserverRequest.premium = checkIntegerValue(premium, null);
		AdserverRequest.keywords = keywords;
		AdserverRequest.minSizeX = checkIntegerValue(minSizeX, null);
		AdserverRequest.minSizeY = checkIntegerValue(minSizeY, null);
		AdserverRequest.maxSizeX = checkIntegerValue(maxSizeX, null);
		AdserverRequest.maxSizeY = checkIntegerValue(maxSizeY, null);
		AdserverRequest.paramBG = paramBG;
		AdserverRequest.paramLINK = paramLINK;
		AdserverRequest.customParameters = customParameters;
		AdserverRequest.adServerUrl = AdserverURL.getUrl();
		AdserverRequest.latitude = latitude;
		AdserverRequest.longitude = longitude;
		AdserverRequest.country = country;
		AdserverRequest.region = region;
//		AdserverRequest.city = city;
//		AdserverRequest.area = area;
//		AdserverRequest.metro = metro;
//		AdserverRequest.zip = zip;
		AdserverRequest.carrier = carrier;
		AdserverRequest.connectionSpeed = checkIntegerValue(connectionSpeed, null);
		AdserverRequest.key = Constants.KEY;
		AdserverRequest.adsType = Constants.ADS_TYPE;
		AdserverRequest.count = Constants.COUNT;
	}

	/**
	 * Build result URL
	 * 
	 * @return URL
	 */
	public static String createURL() {

		URLParamEncoder encoder = new URLParamEncoder();

		if (null != site) {
			// The id of the publisher site (default: DEFAULT_SITE).
			encoder.addParam("site", site);
		}
		if (null != zone) {
			// The id of the zone of publisher site (default: DEFAULT_ZONE).
			encoder.addParam("zone", zone);
		}
		if (null != ua) {
			// The browser user agent of the device making the request.
			encoder.addParam("ua", ua);
		}
		if (null != testMode) {
			// Default setting is test mode where, if the ad code is properly installed, the ad response is "Test MODE" (default: DEFAULT_TEST_MODE).
			encoder.addParam("test", testMode);
		}
		if (null != premium) {
			// Filter by premium:
			//      PREMIUM_STATUS_NON_PREMIUM - non-premium,
			//      PREMIUM_STATUS_PREMIUM - premium only,
			//      PREMIUM_STATUS_BOTH - both).
			encoder.addParam("premium", premium.intValue());
		}
		if (null != keywords) {
			// Keywords to search ad delimited by commas.
			encoder.addParam("keywords", keywords);
		}
		if (null != minSizeX) {
			encoder.addParam("min_size_x", minSizeX.intValue());
		}
		if (null != minSizeY) {
			encoder.addParam("min_size_y", minSizeY.intValue());
		}
		if (null != maxSizeX) {
			encoder.addParam("size_x", maxSizeX.intValue());
		}
		if (null != maxSizeX) {
			encoder.addParam("size_y", maxSizeX.intValue());
		}
		if (null != paramBG) {
			// Background color (#XXXXXX)
			encoder.addParam("paramBG", paramBG);
		}
		if (null != paramLINK) {
			// Background color (#XXXXXX)
			encoder.addParam("paramLINK", paramLINK);
		}
		if (null != customParameters) {
			//Custom Parameters comma separated
			encoder.addParam("customParameters", customParameters);
		}
		if (null != latitude) {
			encoder.addParam("lat", latitude);
		}
		if (null != longitude) {
			encoder.addParam("long", longitude);
		}
		if (null != country) {
			// Country of visitor. Will override country detected by IP. (http://www.mojiva.com/docs/iso3166.csv)
			encoder.addParam("country", country);
		}
		if (null != region) {
			// Region of visitor. Codes for US and Canada - http://www.mojiva.com/docs/iso3166_2.csv, others - http://www.mojiva.com/docs/fips10_4.csv.
			encoder.addParam("region", region.toUpperCase());
		}
		if (null != carrier) {
			// Carrier name
			encoder.addParam("carrier", carrier);
		}
		if (null != city) {
			encoder.addParam("city", city);
		}
		if (null != area) {
			encoder.addParam("area", area);
		}
		if (null != metro) {
			encoder.addParam("metro", metro);
		}
		if (null != zip) {
			encoder.addParam("ZIP", zip);
		}
		if (null != carrier) {
			encoder.addParam("carier", carrier);
		}
		if (null != connectionSpeed) {
			encoder.addParam("connection_speed", connectionSpeed.toString());
		}

		if (null != key) {
			encoder.addParam("key", key.intValue());
		}
		if (null != adsType) {
			// Type of advertisement:
			//      ADS_TYPE_TEXT_ONLY - text only,
			//      ADS_TYPE_IMAGES_ONLY - image only,
			//      ADS_TYPE_TEXT_AND_IMAGES - image and text,
			//      ADS_TYPE_SMS - SMS ad, default - DEFAULT_ADS_TYPE).
			encoder.addParam("adstype", adsType.intValue());
		}
		if (null != count) {
			// Quantity of ads, returned by a server (Maximum: 5; Default: DEFAULT_COUNT).
			encoder.addParam("count", count.intValue());
		}
		//SDK vertion 
		encoder.addParam("version", Constants.VERSION);
		
		return adServerUrl + encoder.toString();
	}

	/**
	 * Check String value
	 * 
	 * @param value
	 *            Check value
	 * @param defaultValue
	 *            returns if checking value fail
	 * @return checked value
	 */
	public static String checkStringValue(final String value, final String defaultValue) {
		return (null != value && value.length() > 0) ? value : defaultValue;
	}

	/**
	 * Check Boolean value
	 * 
	 * @param value
	 *            Check value
	 * @param defaultValue
	 *            returns if checking value fail
	 * @return checked value
	 */
	public static Boolean checkBooleanValue(final Boolean value, final Boolean defaultValue) {
		return (null != value) ? value : defaultValue;
	}

	/**
	 * Check Integer value
	 * 
	 * @param value
	 *            Check value
	 * @param defaultValue
	 *            returns if checking value fail
	 * @return checked value
	 */
	public static Integer checkIntegerValue(final Integer value, final Integer defaultValue) {
		return (null != value) ? value : defaultValue;
	}
}
