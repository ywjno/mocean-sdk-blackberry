package com.adserver.utils;

import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.ui.UiApplication;

import java.io.UnsupportedEncodingException;

public class Utils {

	public static String getMD5Hash(String hash) {
		return getMD5Hash(hash, 1);
	}
	
	

	public static String getMD5Hash(String hash, int count) {
		String hashTmp = hash;
		MD5Digest digest = new MD5Digest();
		for (int i = 0; i < count; ++i) {
			try {
				digest.update(hashTmp.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				digest.update(hashTmp.getBytes());
			}
			hashTmp = byteArrayToHexString(digest.getDigest());
		}
		return hashTmp;
	}

	private static String byteArrayToHexString(byte[] array) {
		int size = array.length;
		StringBuffer hexString = new StringBuffer(size);
		for (int i = 0; i < size; ++i) {
			int intVal = array[i] & 0xff;
			if (intVal < 0x10)
				hexString.append('0');
			hexString.append(Integer.toHexString(intVal));
		}
		return hexString.toString();
	}
	
	//scrape utility class
	public static String scrape(String resp, String start, String stop) {
		if (null == resp ) return null;
		int offset, len;
		if((offset = resp.indexOf(start)) < 0)
			return "";
		if((len = resp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}
	
	public static String replaceAll(String source, String pattern, String replacement) {
        if (source == null) {
            return "";
        }
       
        StringBuffer sb = new StringBuffer();
        int idx = -1;
        int patIdx = 0;

        while ((idx = source.indexOf(pattern, patIdx)) != -1) {
            sb.append(source.substring(patIdx, idx));
            sb.append(replacement);
            patIdx = idx + pattern.length();
        }
        sb.append(source.substring(patIdx));
        return sb.toString();

    }
	
	public static void isEDT() {
		System.out.println("EDT: " +UiApplication.isEventDispatchThread());
	}

}
