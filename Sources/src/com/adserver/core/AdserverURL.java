/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Fri Mar 11 17:34:56 MSK 2011 */
package com.adserver.core;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class AdserverURL {


   /** buildDate (set during build process to 1299854096784L). */
   private static Date buildDate = new Date(1299854096784L);

   /**
    * Get buildDate (set during build process to Fri Mar 11 17:34:56 MSK 2011).
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


   /** version (set during build process to "2.0.0 beta"). */
   private static String version = "2.1";

   /**
    * Get version (set during build process to "2.0.0 beta").
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
