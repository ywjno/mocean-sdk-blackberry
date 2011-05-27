package com.adserver.utils;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EventLogger;

import java.util.Date;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class Logger {
    public static final long GUID = 0x4b84b6736ec2e7baL;
    private static final String LOG_NAME = "Adserver";

    private static final String appName = "Adserver";

    // logLevel 1=debug, 2=info 3=network
    private int logLevel = 2;
    private String hashId = "";

    //Used to format dates into a standard format
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");

	public Logger(String hashId) {
    	this.hashId = hashId;
    	register();
    }
    
    public static void register() {
        EventLogger.register(GUID, LOG_NAME, EventLogger.VIEWER_STRING);
    }

    public void debug(String msg) {
        if (logLevel >= 1) {
            msg = setUpMessageString("DEBUG", msg);
            EventLogger.logEvent(GUID, msg.getBytes());
            System.out.println(msg);
        }
    }

    public void info(String msg) {
        if (logLevel >= 2) {
            msg = setUpMessageString("INFO", msg);
            EventLogger.logEvent(GUID, msg.getBytes());
            System.out.println(msg);
        }
    }

    public void network(String msg) {
        if (logLevel >= 3) {
            msg = setUpMessageString("NETWORK", msg);
            EventLogger.logEvent(GUID, msg.getBytes());
            System.out.println(msg);
        }
    }
    

    private String setUpMessageString(String level, String msg) {
        Date timestamp = new Date();
        StringBuffer sb = new StringBuffer();
        sb.append(appName + " :");
        sb.append(hashId);
        sb.append(" *** " + level + "*** ");
        sb.append(" - ");
        sb.append(dateFormat.format(timestamp));
        sb.append(": ");
        sb.append(msg);
        return sb.toString();
    }

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public String getHashId() {
		return hashId;
	}

	public void setHashId(String hashId) {
		this.hashId = hashId;
	}

}