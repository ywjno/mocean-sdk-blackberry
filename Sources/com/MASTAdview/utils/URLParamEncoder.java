package com.MASTAdview.utils;

/**
 * Encoding URL query strings <br>
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class URLParamEncoder {

	String	szUrlParam	= null;

	public URLParamEncoder() {
	}

	private void prepAdd() {
		if (szUrlParam == null) {
			szUrlParam = "";
		} else {
			szUrlParam += "&";
		}
	}

	public void addParam(String szName, String szValue) {
		prepAdd();
		String test1 = urlEncode(szName);
		String test2 = urlEncode(szValue);
		szUrlParam += test1 + '=' + test2;
//		szUrlParam += urlEncode(szName) + '=' + urlEncode(szValue);
	}

	public void addParam(String szName, int nValue) {
		prepAdd();
		szUrlParam += urlEncode(szName) + '=' + urlEncode(Integer.toString(nValue));
	}

	public void addParam(final String szName, boolean bValue) {
		prepAdd();
		Boolean boolValue = bValue ? Boolean.TRUE : Boolean.FALSE;
		szUrlParam += urlEncode(szName) + '=' + urlEncode(boolValue.toString());
	}

	public static String urlEncode(final String s) {
		if (null == s) {
			return "";
		}

		int length = s.length();
		StringBuffer sb = new StringBuffer(length);

		for (int i = 0; i < length; ++i) {
			switch (s.charAt(i)) {
			case ' ':
				sb.append("%20");
				break;
			case '+':
				sb.append("%2b");
				break;
			case '\'':
				sb.append("%27");
				break;
			case '<':
				sb.append("%3c");
				break;
			case '>':
				sb.append("%3e");
				break;
			case '#':
				sb.append("%23");
				break;
			case '%':
				sb.append("%25");
				break;
			case '{':
				sb.append("%7b");
				break;
			case '}':
				sb.append("%7d");
				break;
			case '\\':
				sb.append("%5c");
				break;
			case '^':
				sb.append("%5e");
				break;
			case '~':
				sb.append("%73");
				break;
			case '[':
				sb.append("%5b");
				break;
			case ']':
				sb.append("%5d");
				break;
			default:
				sb.append(s.charAt(i));
				break;
			}
		}

		return sb.toString();
	}

	public String toString() {
		return szUrlParam;
	}

}
