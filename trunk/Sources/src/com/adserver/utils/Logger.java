package com.adserver.utils;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EventLogger;

import java.util.Date;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class Logger {
    public static final long GUID = 0x3b94b6736ec2e8baL;
    private static final String LOG_NAME = "Adserver";

    private static final String appName = "Adserver";

    // logLevel 1=debug, 2=info
    private static final int logLevel = 1;

    //Used to format dates into a standard format
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");

    public static void register() {
        EventLogger.register(GUID, LOG_NAME, EventLogger.VIEWER_STRING);
    }

    public static void info(String msg) {
        if (logLevel <= 2) {
            msg = setUpMessageString("INFO", msg);
            EventLogger.logEvent(GUID, msg.getBytes());
            System.out.println(msg);
        }
    }

    public static void debug(String msg) {
        if (logLevel <= 1) {
            msg = setUpMessageString("DEBUG", msg);
            EventLogger.logEvent(GUID, msg.getBytes());
            System.out.println(msg);
        }
    }

    public static void error(Throwable t, String className, String idOfBlock) {
        String msg = setUpErrorString(className + ", Id : " + idOfBlock + " error: " + t.toString());
        t.printStackTrace();
        EventLogger.logEvent(GUID, msg.getBytes());
        System.err.println(msg);

    }

    private static String setUpMessageString(String level, String msg) {
        Date timestamp = new Date();
        StringBuffer sb = new StringBuffer();
        sb.append("*** " + level + "*** ");
        sb.append(appName);
        sb.append(" - ");
        sb.append(dateFormat.format(timestamp));
        sb.append(": ");
        sb.append(msg);
        return sb.toString();
    }

    private static String setUpErrorString(String error) {
        Date timestamp = new Date();
        StringBuffer sb = new StringBuffer();
        sb.append("*** ERROR ***");
        sb.append(appName);
        sb.append(" - ");
        sb.append(dateFormat.format(timestamp));
        sb.append(": ");
        sb.append(error);
        return sb.toString();
    }
}