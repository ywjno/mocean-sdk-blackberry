/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Thu Feb 10 11:54:42 MSK 2011 */
package com.adserver.core;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class AdserverURL {


   /** buildDate (set during build process to 1297328082720L). */
   private static Date buildDate = new Date(1297328082720L);

   /**
    * Get buildDate (set during build process to Thu Feb 10 11:54:42 MSK 2011).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /** project (set during build process to "Adserver"). */
   private static String project = "Adserver";

   /**
    * Get project (set during build process to "Adserver").
    * @return String project
    */
   public static final String getProject() { return project; }


   /** version (set during build process to "1.6.3"). */
   private static String version = "1.6.3";

   /**
    * Get version (set during build process to "1.6.3").
    * @return String version
    */
   public static final String getVersion() { return version; }


   /** url (set during build process to "http://ads.mocean.mobi/ad?"). */
   private static String url = "http://ads.mocean.mobi/ad?";

   /**
    * Get url (set during build process to "http://ads.mocean.mobi/ad?").
    * @return String url
    */
   public static final String getUrl() { return url; }

}
