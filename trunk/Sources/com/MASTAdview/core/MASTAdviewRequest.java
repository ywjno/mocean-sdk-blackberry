package com.MASTAdview.core;

import java.util.Enumeration;
import java.util.Hashtable;

import com.MASTAdview.utils.Constants;
import com.MASTAdview.utils.URLParamEncoder;

public class MASTAdviewRequest {
	private Hashtable parameters = new Hashtable();
	private final String parameter_site = "site";
	private final String parameter_zone = "zone";
	private final String parameter_userAgent = "ua";
	private final String parameter_keywords = "keywords";
	private final String parameter_premium = "premium";
	private final String parameter_test = "test";
	private final String parameter_count = "count";
	private final String parameter_country = "country";
	private final String parameter_region = "region";
	private final String parameter_city = "city";
	private final String parameter_area = "area";
	private final String parameter_metro = "metro";
	private final String parameter_zip = "ZIP";
	private final String parameter_type = "type";
	private final String parameter_key = "key";	
	private final String parameter_latitude = "lat";
	private final String parameter_longitude = "long";
	private final String parameter_background = "paramBG";
	private final String parameter_link = "paramLINK";
	private final String parameter_carrier = "carrier";
	private final String parameter_min_size_x = "min_size_x";
	private final String parameter_min_size_y = "min_size_y";
	private final String parameter_size_x = "size_x";
	private final String parameter_size_y = "size_y";
	private final String parameter_excampaigns = "excampaigns";
	private final String parameter_version = "version";
	private final String parameter_connection_speed = "connection_speed";
	private final String parameter_size_required = "size_required";
	private final String parameter_mcc = "mcc";
	private final String parameter_mnc = "mnc";
	
	private final String parameter_timeout = "timeout";
	
	private AdserverBase adserver;

	
	

	private Hashtable customParameters;

	private String adServerUrl = "http://ads.mocean.mobi/ad";

	public String getAdServerUrl() {
		return adServerUrl;
	}
	
	public void setAdServerUrl(String adServerUrl){
		this.adServerUrl = adServerUrl;
	}

	public MASTAdviewRequest() {
	}
	
	public MASTAdviewRequest(String site, String zone) {
		setSite(site);
		setZone(zone);
	}

	/**
	 * Required.
	 * Set the id of the publisher site. 
	 * @param site
	 *            Id of the site assigned by Adserver
	 * @return
	 */
	public MASTAdviewRequest setSite(String site) {
		if(site != null) {
			synchronized(parameters) {
				parameters.put(parameter_site, site);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set the browser user agent of the device making the request.
	 * @param ua
	 * @return
	 */
	public MASTAdviewRequest setUa(String ua) {
		if(ua != null) {
			synchronized(parameters) {
				parameters.put(parameter_userAgent, ua);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Keywords to search ad delimited by commas.
	 * @param keywords
	 * @return
	 */
	public MASTAdviewRequest setKeywords(String keywords) {
		if(keywords != null) {
			synchronized(parameters) {
				parameters.put(parameter_keywords, keywords);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, 
	 * PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). 
	 * Can be used only by premium publishers.
	 * @param premium
	 * @return
	 */
	public MASTAdviewRequest setPremium(Integer premium) {
		if(premium != null) {
			synchronized(parameters) {
				parameters.put(parameter_premium, String.valueOf(premium));
			}
		}
		return this;	
	}

	/**
	 * Required.
	 * Set the id of the zone of publisher site.
	 * @param zone
	 * @return
	 */
	public MASTAdviewRequest setZone(String zone) {
		if(zone != null) {
			synchronized(parameters) {
				parameters.put(parameter_zone, zone);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Default setting is test mode where, if the ad code is properly installed, 
	 * the ad response is "Test MODE".
	 * @param enabled
	 * @return
	 */
	public MASTAdviewRequest setTestModeEnabled(Boolean enabled) {
		if(enabled != null) {
			synchronized(parameters) {
				if(enabled.booleanValue()) {
					parameters.put(parameter_test, "1");
				} else {
					parameters.put(parameter_test, "0");
				}
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Quantity of ads, returned by a server. Maximum value is 5.
	 * @param count
	 * @return
	 */
	public MASTAdviewRequest setCount(Integer count) {
		if(count != null) {
			synchronized(parameters) {
				parameters.put(parameter_count, String.valueOf(count));
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Country of visitor. See codes here (http://www.mojiva.com/docs/iso3166.csv). 
	 * Will override country detected by IP. 
	 * @param country
	 * @return
	 */
	public MASTAdviewRequest setCountry(String country) {
		if(country != null) {
			synchronized(parameters) {
				parameters.put(parameter_country, country);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Region of visitor. See codes for US and Canada here (http://www.mojiva.com/docs/iso3166_2.csv), 
	 * others - here (http://www.mojiva.com/docs/fips10_4.csv). 
	 * @param region
	 * @return
	 */
	public MASTAdviewRequest setRegion(String region) {
		if(region != null) {
			synchronized(parameters) {
				parameters.put(parameter_region, region);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set City of the device user (with state). For US only. 
	 * @param city
	 * @return
	 */
	public MASTAdviewRequest setCity(String city) {
		if(city != null) {
			synchronized(parameters) {
				parameters.put(parameter_city, city);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Area code of a user. For US only. 
	 * @param area
	 * @return
	 */
	public MASTAdviewRequest setArea(String area) {
		if(area != null) {
			synchronized(parameters) {
				parameters.put(parameter_area, area);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Metro code of a user. For US only. 
	 * @param metro
	 * @return
	 */
	public MASTAdviewRequest setMetro(String metro) {
		if(metro != null) {
			synchronized(parameters) {
				parameters.put(parameter_metro, metro);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Zip/Postal code of user. For US only. 
	 * @param zip
	 * @return
	 */
	public MASTAdviewRequest setZip(String zip) {
		if(zip != null) {
			synchronized(parameters) {
				parameters.put(parameter_zip, zip);
			}
		}
		return this;
	}
	
	/**
	 * Type of ads to be returned (1 - text, 2 - image, 4 - richmedia ad). 
	 * You can set different combinations with these values. 
	 * For example, 3 = 1 + 2 (text + image), 7 = 1 + 2 + 4 (text + image + richmedia) and so on. 
	 * Default value is -1 that means any type of ads can be returned.
	 * @param type
	 * @return
	 */
	
	public MASTAdviewRequest setType(Integer type) {
		if(type != null) {
			synchronized(parameters) {
				if ((type.intValue() >= -1) && (type.intValue() <= 7)) parameters.put(parameter_type, String.valueOf(type));
			}
		}
		return this;
	}

	public MASTAdviewRequest setKey(Integer key) {
		if(key != null) {
			synchronized(parameters) {
				parameters.put(parameter_key, String.valueOf(key));
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Latitude.
	 * @param latitude
	 * @return
	 */
	public MASTAdviewRequest setLatitude(String latitude) {
		if(latitude != null) {
			synchronized(parameters) {
				parameters.put(parameter_latitude, latitude);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Longitude.
	 * @param longitude
	 * @return
	 */
	public MASTAdviewRequest setLongitude(String longitude) {
		if(longitude != null) {
			synchronized(parameters) {
				parameters.put(parameter_longitude, longitude);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Background color in borders.
	 * @param paramBG
	 * @return
	 */
	public MASTAdviewRequest setParamBG(String paramBG) {
		if(paramBG != null) {
			synchronized(parameters) {
				parameters.put(parameter_background, paramBG);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Text color.
	 * @param paramLINK
	 * @return
	 */
	public MASTAdviewRequest setParamLINK(String paramLINK) {
		if(paramLINK != null) {
			synchronized(parameters) {
				parameters.put(parameter_link, paramLINK);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Carrier name.
	 * @param carrier
	 * @return
	 */
	public MASTAdviewRequest setCarrier(String carrier) {
		if(carrier != null) {
			synchronized(parameters) {
				parameters.put(parameter_carrier, carrier);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set minimum width of advertising. 
	 * @param minSizeX
	 * @return
	 */
	public MASTAdviewRequest setMinSizeX(Integer minSizeX) {
		if(minSizeX != null) {
			synchronized(parameters) {
				parameters.put(parameter_min_size_x, String.valueOf(minSizeX));
			}
		}
		return this;	
	}

	/**
	 * Optional.
	 * Set minimum height of advertising. 
	 * @param minSizeY
	 * @return
	 */
	public MASTAdviewRequest setMinSizeY(Integer minSizeY) {
		if(minSizeY != null) {
			synchronized(parameters) {
				parameters.put(parameter_min_size_y, String.valueOf(minSizeY));
			}
		}
		return this;	
	}

	/**
	 * Optional.
	 * Set maximum width of advertising. 
	 * @param sizeX
	 * @return
	 */
	public MASTAdviewRequest setSizeX(Integer sizeX) {
		if(sizeX != null) {
			synchronized(parameters) {
				parameters.put(parameter_size_x, String.valueOf(sizeX));
			}
		}
		return this;	
	}

	/**
	 * Optional.
	 * Set maximum height of advertising. 
	 * @param sizeY
	 * @return
	 */
	public MASTAdviewRequest setSizeY(Integer sizeY) {
		if(sizeY != null) {
			synchronized(parameters) {
				parameters.put(parameter_size_y, String.valueOf(sizeY));
			}
		}
		return this;	
	}

	/**
	 * Optional.
	 * Parameter excampaigns should allow excluding the list of campaigns from the result by ID. 
	 * @param excampaigns
	 * @return
	 */
	public MASTAdviewRequest setExcampaigns(String excampaigns) {
		if((excampaigns != null) && (excampaigns.length() > 0)) {
			synchronized(parameters) {
				parameters.put(parameter_excampaigns, excampaigns);
			}
		}
		return this;	
	}
	
	/**
	 * Optional.
	 * Set SDK version. 
	 * @param version
	 * @return
	 */
	public MASTAdviewRequest setVersion(String version) {
		if(version != null) {
			synchronized(parameters) {
				parameters.put(parameter_version, version);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set connection speed. 0 - low (gprs, edge), 1 - fast (3g, wifi). 
	 * @param connectionSpeed
	 * @return
	 */
	public MASTAdviewRequest setConnectionSpeed(Integer connectionSpeed) {
		if(connectionSpeed != null) {
			synchronized(parameters) {
				parameters.put(parameter_connection_speed, String.valueOf(connectionSpeed));
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * If set to 1, return image size (width and height) in html. 
	 * @param sizeRequired
	 * @return
	 */
	public MASTAdviewRequest setSizeRequired(Integer sizeRequired) {
		if(sizeRequired != null) {
			synchronized(parameters) {
				parameters.put(parameter_size_required, String.valueOf(sizeRequired));
			}
		}
		return this;
	}
	
	public String getSite() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_site);
		}
	}

	public String getUa() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_userAgent);
		}
	}
	
	public String getKeywords() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_keywords);
		}
	}

	public Integer getPremium() {
		synchronized(parameters) {
			String premium = (String)parameters.get(parameter_premium);
			return getIntParameter(premium);
		}
	}

	public String getZone() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_zone);
		}
	}
	
	public Boolean getTestModeEnabled() {
		synchronized(parameters) {
			String test = (String)parameters.get(parameter_test);
			if(test != null) {
				if(test.equals("1")) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			} else {
				return Boolean.FALSE;
			}
		}
	}
	
	public Integer getCount() {
		synchronized(parameters) {
			String count = (String)parameters.get(parameter_count);
			return getIntParameter(count);
		}
	}

	public String getCountry() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_country);
		}
	}
	
	public String getRegion() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_region);
		}
	}

	public String getCity() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_city);
		}
	}

	public String getArea() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_area);
		}
	}

	public String getMetro() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_metro);
		}
	}

	public String getZip() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_zip);
		}
	}
	
	public Integer getType() {
		synchronized(parameters) {
			String type = (String)parameters.get(parameter_type);
			return getIntParameter(type);
		}
	}
	
	public Integer getKey() {
		synchronized(parameters) {
			String key = (String)parameters.get(parameter_key);
			return getIntParameter(key);
		}
	}
	
	public String getLatitude() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_latitude);
		}
	}

	public String getLongitude() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_longitude);
		}
	}

	public String getParamBG() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_background);
		}
	}

	public String getParamLINK() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_link);
		}
	}
	
	public String getCarrier() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_carrier);
		}
	}

	public Integer getMinSizeX() {
		synchronized(parameters) {
			String minSizeX = (String)parameters.get(parameter_min_size_x);
			return getIntParameter(minSizeX);
		}
	}

	public Integer getMinSizeY() {
		synchronized(parameters) {
			String minSizeY = (String)parameters.get(parameter_min_size_y);
			return getIntParameter(minSizeY);
		}
	}

	public Integer getSizeX() {
		synchronized(parameters) {
			String sizeX = (String)parameters.get(parameter_size_x);
			return getIntParameter(sizeX);
		}
	}

	public Integer getSizeY() {
		synchronized(parameters) {
			String sizeY = (String)parameters.get(parameter_size_y);
			return getIntParameter(sizeY);
		}
	}
	
	public String getExcampaigns() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_excampaigns);
		}
	}
	
	public String getVersion() {
		synchronized(parameters) {
			return (String)parameters.get(parameter_version);
		}
	}

	public Integer getConnectionSpeed() {
		synchronized(parameters) {
			String connectionSpeed = (String)parameters.get(parameter_connection_speed);
			return getIntParameter(connectionSpeed);
		}
	}

	public Integer getSizeRequired() {
		synchronized(parameters) {
			String sizeRequired = (String)parameters.get(parameter_size_required);
			return getIntParameter(sizeRequired);
		}
	}
	
	/**
	 * Optional.
	 * Set Custom parameters.
	 * @param customParameters
	 * @return
	 */
	public void setCustomParameters(Hashtable customParameters) {
		this.customParameters = customParameters;
	}

	public Hashtable getCustomParameters() {
		return customParameters;
	}


	private Integer getIntParameter(String stringValue) {
		if(stringValue != null) {
			return new Integer(Integer.parseInt(stringValue));
		} else {
			return null;
		}
	}

	/**
	 * Creates URL with given parameters.
	 * @return
	 * @throws IllegalStateException if all the required parameters are not present.
	 */
	public synchronized String createURL() throws IllegalStateException
	{
		URLParamEncoder encoder = new URLParamEncoder();
		Enumeration keys;
		String key;
		String value;
		//Iterate Parameters
		keys = parameters.keys();
		while (keys.hasMoreElements()) {
			key = (String) keys.nextElement();
			value = (String) parameters.get(key);
			if (null != value) encoder.addParam(key, value);
		}

		//Iterate Custom Parameters
		if (null != customParameters) {
			keys = customParameters.keys();
			while (keys.hasMoreElements()) {
				key = (String) keys.nextElement();
				value = (String) customParameters.get(key);
				if (null != value) encoder.addParam(key, value);
			}
		}
//		//mcc
//		String cellMCC = Integer.toString(GPRSInfo.getCellInfo().getMCC());
//		if (null != cellMCC) {
//			encoder.addParam(parameter_mcc, cellMCC);
//		}
//		//mnc
//		String cellMNC = Integer.toString(GPRSInfo.getCellInfo().getMNC());
//		if (null != cellMNC) {
//			encoder.addParam(parameter_mnc, cellMNC);
//		}
		encoder.addParam("count", Constants.COUNT.intValue());
		//SDK vertion 
		encoder.addParam("version", AdserverURL.getVersion());
		//udid
		encoder.addParam("udid", AutoDetectParameters.getInstance().getMd5DeviceId());
		//timeout
		encoder.addParam("timeout", Integer.toString(Constants.timeout_set.intValue()));
		//size_x

//		encoder.addParam("size_x", Constants.size_x);
//		encoder.addParam("size_y", Constants.size_y);
		
		//
		if (null!= adserver) {
			encoder.addParam("size_x", adserver.getPreferredWidth());
			encoder.addParam("size_y", adserver.getPreferredHeight());
		}
		return getAdServerUrl()  + "?" + encoder.toString();
	}
	
	//13.02.2011
	/*
	 * Specify timeout of ad call. This tells the ad server the maximum time you are willing to 
	 * wait for an ad response (default: 1000ms, max: 3000ms)
	 */
	public void setTimeout(Integer timeout) {
		if(timeout != null) {
			if (timeout.intValue() < 1000) Constants.timeout_set = new Integer(1000);
			if (timeout.intValue() > 3000) Constants.timeout_set= new Integer(3000);
			
		}
	}
	
	public Integer getTimeout() {
		synchronized(parameters) {
			return Constants.timeout_set;
		}
	}

	public void setAdserver(AdserverBase thisPtr) {
		this.adserver = thisPtr;
	}
}
