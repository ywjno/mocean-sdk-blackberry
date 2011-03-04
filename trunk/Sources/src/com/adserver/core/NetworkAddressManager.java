package com.adserver.core;

import com.adserver.net.HttpUtils;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class NetworkAddressManager {
    private final static String URL = "http://www.whatismyip.org";
    private String ip = "";

    private static NetworkAddressManager instance = null;
    private Thread thread = null;


    private NetworkAddressManager() {
        super();
    }

    public synchronized static NetworkAddressManager getInstance() {
        if (null == instance) {
            instance = new NetworkAddressManager();
        }
        return instance;
    }

    private void load() {
        if (null == thread || !thread.isAlive()) {
            thread = new Thread() {
                public void run() {
                    fetchIP();
                }
            };
            thread.start();
        }
    }

    public final void fetchIP() {
    	try {
			ip = HttpUtils.get(URL);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	/* DEPRECATED
        try {
            HttpConnection hc = null;
            try {
                hc = (HttpConnection) Connector.open(URL, Connector.READ_WRITE, true);
                hc.setRequestMethod(HttpConnection.GET);
                if (hc.getResponseCode() == HttpConnection.HTTP_OK) {
                    InputStream is = null;
                    try {
                        is = hc.openInputStream();
                        byte[] data = IOUtilities.streamToBytes(is);
                        this.ip = new String(data);
                    } finally {
                        if (null != is) {
                            is.close();
                        }
                    }
                }
            } finally {
                if (null != hc) {
                    hc.close();
                }
            }
        } catch (IOException ignored) {
        }
        */
    }

    public String getIP() {
        if (ip.length() == 0) {
            load();
        }
        return ip;
    }
}
