package com.MASTAdview.browser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import com.MASTAdview.utils.Utils;

import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.browser.field2.BrowserFieldResponse;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.http.HttpHeaders;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
public class CacheManagerImpl implements CacheManager {

    private static final int MAX_STANDARD_CACHE_AGE = 2592000;
    public static final String ADSERVER_ROOT = "Adserver/"; // Main workspace name
    public static final String ADSERVER_CACHE = "cache/";   // subdir for cache
    private String rootpath = "";   // Path to main workspace
    private CacheItem lastSavedCacheItem = null;

	
	public CacheManagerImpl() {
//		Create cache directory
		try {
            rootpath = findRootpath();
        } catch (IOException e) {
            rootpath = "file:///store/home/user/" + ADSERVER_ROOT;
        }

	}
	
    public boolean isRequestCacheable(BrowserFieldRequest request) {
        // Only HTTP requests are cacheable
        if (!request.getProtocol().equals("http")) {
            return false;
        }
        
        // Don't cache the request whose method is not "GET".
        if (request instanceof HttpConnection) {
            if (!((HttpConnection) request).getRequestMethod().equals("GET")) {
                return false;
            }
        }
        
        // Don't cache the request with post data.
        if (request.getPostData() != null) {
                return false;
        }
                
        // Don't cache authentication request.
        if (request.getHeaders().getPropertyValue("Authorization") != null) {
            return false;
        }        
        
        return true;    	
    }
    
    public boolean isResponseCacheable(HttpConnection response) {
        try {
            if (response.getResponseCode() != 200) {
                return false;
            }
        } catch (IOException ioe) {
            return false;
        }

        if (!response.getRequestMethod().equals("GET")) {
            return false;
        }
        
        if (containsPragmaNoCache(response)) {
            return false;
        }

        if (isExpired(response)) {
            return false;
        }
        
        if (containsCacheControlNoCache(response)) {
            return false;
        }
        
        if ( response.getLength() <= 0 ) {
        	return false;
        }
        
        // additional checks can be implemented here to inspect
        // the HTTP cache-related headers of the response object
       
        return true;
    }
    
    private boolean isExpired(HttpConnection response) {
        try {
            long expires = response.getExpiration(); // getExpiration() returns 0 if not known
            if (expires > 0 && expires <= (new Date()).getTime()) {
                return true;
            }

            return false;
        } catch (IOException ioe) {
            return true;
        }
    }
    
    private boolean containsPragmaNoCache(HttpConnection response) {
        try {
            if (response.getHeaderField("pragma") != null && response.getHeaderField("pragma").toLowerCase().indexOf("no-cache") >= 0) {
                return true;
            } 
            
            return false;
        } catch (IOException ioe) {
            return true;
        }
    }
    
    private boolean containsCacheControlNoCache(HttpConnection response) {
        try {
            String cacheControl = response.getHeaderField("cache-control");
            if (cacheControl != null) {
                cacheControl = removeSpace(cacheControl.toLowerCase());
                if (cacheControl.indexOf("no-cache") >= 0 
                    || cacheControl.indexOf("no-store") >= 0 
                    || cacheControl.indexOf("private") >= 0 
                    || cacheControl.indexOf("max-age=0") >= 0) {
                    return true;        
                }
                
                long maxAge = parseMaxAge(cacheControl);
                if (maxAge > 0 && response.getDate() > 0) {
                    long date = response.getDate();
                    long now = (new Date()).getTime();                    
                    if (now > date + maxAge) {
                        // Already expired
                        return true;
                    }
                }
            } 

            return false;
        } catch (IOException ioe) {
            return true;
        }
    }    
    
    public InputConnection createCache(String url, HttpConnection response) {

        byte[] data = null;
        InputStream is = null;

        // Calculate expires
        long expires = calculateCacheExpires(response);
        
        // Copy headers
        HttpHeaders headers = copyResponseHeaders(response);
        
        try {
            // Read data
            int len = (int) response.getLength();
            if (len > 0) {
                is = response.openInputStream();
                int actual = 0;
                int bytesread = 0 ;
                data = new byte[len];
                while ((bytesread != len) && (actual != -1)) {
                    actual = is.read(data, bytesread, len - bytesread);
                    bytesread += actual;
                }
            } else {
            	data = IOUtilities.streamToBytes(is);
            }       
        } catch (IOException ioe) {
            System.out.println("Exceprion : " + ioe.getMessage());
        	data = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                	System.out.println("Exceprion : " + ioe.getMessage());
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ioe) {
                	System.out.println("Exceprion : " + ioe.getMessage());
                }
            } 
        }

        if (data == null) {
            return null;
        } 

        // add item to cache
//        cacheTable.put(url, new CacheItem(url, expires, data, headers));
        try {
            cachePut(url, new CacheItem(url, expires, data, headers));
		} catch (Exception e) {
			System.out.println("Exceprion cachePut: " + e.getMessage());
		}
        
        
        return new BrowserFieldResponse(url, data, headers);
    }
    
    public String createCacheWithoutMetadata(String url, HttpConnection response) {
        InputStream is = null;
        FileConnection conn = null;
        OutputStream os = null;
        DataOutputStream dos = null;
        int writeCount = 0;
        String contentType;
        
        //create file
        String md5HashFileName = Utils.getMD5Hash(url);
        try {
            //guess content type
            contentType = response.getHeaderField("Content-Type");
        	conn = (FileConnection) Connector.open(getCachepath() + md5HashFileName, Connector.READ_WRITE);
            if (!conn.exists()) {
                conn.create();
            }
            //open output stream
            os = conn.openOutputStream();
            //write content type
            dos = new DataOutputStream(os);
            dos.writeUTF(contentType);
            dos.flush();
        } catch (Exception e) {
		}
        
        //read data
        try {
        	is = response.openInputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
			{
				os.write(buf, 0, len);
				writeCount++;
			}
        } catch (IOException ioe) {
        	return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ioe) {
                }
            } 
            if (os != null) {
            	try {
					os.close();
				} catch (IOException ioe) {
				}
            }
            if (dos != null) {
            	try {
					dos.close();
				} catch (IOException ioe) {
				}
            }
            if (conn != null) {
            	try {
            		conn.close();
            	} catch (IOException ioe) {
				}
            }
        }
        if (writeCount >0) return getCachepath() + Utils.getMD5Hash(url);
        else return null;
    }
    
    
    private long calculateCacheExpires(HttpConnection response) {
        long date = 0;
        try {
            date = response.getDate();
        } catch (IOException ioe) {
        }
        
        if (date == 0) {
            date = (new Date()).getTime();
        }

        long expires = getResponseExpires(response);
        
        // If an expire date has not been specified assumes the maximum time
        if ( expires == 0 ) {
        	return date + (MAX_STANDARD_CACHE_AGE * 1000L);
        }
        
        return expires;
    }
    
    private long getResponseExpires(HttpConnection response) {
        try {
            // Calculate expires from "expires"
            long expires = response.getExpiration();
            if (expires > 0) {
                return expires;
            }
            
            // Calculate expires from "max-age" and "date"
            if (response.getHeaderField("cache-control") != null) {
                String cacheControl = removeSpace(response.getHeaderField("cache-control").toLowerCase());
                long maxAge = parseMaxAge(cacheControl);
                long date = response.getDate();
                
                if (maxAge > 0 && date > 0) {
                    return (date + maxAge);
                }
            }
        } catch (IOException ioe) {
        }
        
        return 0;
    }
    
    private long parseMaxAge(String cacheControl) {
        if (cacheControl == null) {
            return 0;
        }
        
        long maxAge = 0;
        if (cacheControl.indexOf("max-age=") >= 0) {
            int maxAgeStart = cacheControl.indexOf("max-age=") + 8;
            int maxAgeEnd = cacheControl.indexOf(',', maxAgeStart);
            if (maxAgeEnd < 0) {
                maxAgeEnd = cacheControl.length();
            }
            
            try {
                maxAge = Long.parseLong(cacheControl.substring(maxAgeStart, maxAgeEnd));
            } catch (NumberFormatException nfe) {
            }
        }
        
                // Multiply maxAge by 1000 to convert seconds to milliseconds
                maxAge *= 1000L;
        return maxAge;
    }
    
    private static String removeSpace(String s) {
        StringBuffer result= new StringBuffer();
        int count = s.length();
        for (int i = 0; i < count; i++) {
            char c = s.charAt(i);
            if (c != ' ') {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private HttpHeaders copyResponseHeaders(HttpConnection response) {
        HttpHeaders headers = new HttpHeaders();
        try {
        	int index = 0;
            while (response.getHeaderFieldKey(index) != null) {
            	headers.addProperty(response.getHeaderFieldKey(index), response.getHeaderField(index));
                index++;
            }
        } catch (IOException ioe) {
        	System.out.print("Exception: " + ioe.getMessage());
        }
        
        return headers;
    }    
    
    public boolean hasCache(String url) {
//    	return cacheTable.containsKey(url);
    	return cacheContains(url);
    	
    }
   
    public boolean hasCacheExpired(String url) {
//        Object o = cacheTable.get(url);
    	Object o = null;
    	try {
    		o = cacheGet(url);
		} catch (Exception e) {
		}
        
        if (o instanceof CacheItem) {
            CacheItem ci = (CacheItem) o;
            long date = (new Date()).getTime();
            if (ci.getExpires() > date) {
                return false;
            } else {
                // Remove the expired cache item
                clearCache(url);
            }
        }
        
        return true;
    }
    
    public void clearCache(String url) {
//        cacheTable.remove(url);
    	cacheRemove(url);
    }    
    
    public InputConnection getCache(String url) {
//        Object o = cacheTable.get(url);
    	Object o = null;
    	try{
    		o = cacheGet(url);
    	}catch (Exception e) {
		}
        if (o instanceof CacheItem) {
            CacheItem ci = (CacheItem) o;
            return new BrowserFieldResponse(url, ci.getData(), ci.getHttpHeaders());
        }        
        return null;
    }
    
    //------------Serialization
    /**
     * Search path to main workspace
     *
     * @return
     * @throws IOException
     */
    private static String findRootpath() throws IOException {
        String root = "";
        String sdcard = "";
        String store = "";

        Enumeration drives = FileSystemRegistry.listRoots();
        
        while (drives.hasMoreElements()) {
            root = (String) drives.nextElement();
            if (root.equalsIgnoreCase("store/")) {
                store = "store/home/user/";
                break;
            }
        }
        drives = FileSystemRegistry.listRoots();
        while (drives.hasMoreElements()) {
            root = (String) drives.nextElement();
            if (root.equalsIgnoreCase("SDCard/")) {
                sdcard = "SDCard/";
                break;
            }
        }

        if (sdcard.equals("")) root = store;
        else root = sdcard;
        
        root = "file:///" + root + ADSERVER_ROOT;
        createDirectory(root);
        createDirectory(root + ADSERVER_CACHE);

        return root;
    }

    public String getCachepath() {
        return rootpath + ADSERVER_CACHE;
    }


    /**
     * Create directory
     *
     * @param path directory full path
     * @throws IOException
     */
    public static void createDirectory(final String path) throws IOException {
        FileConnection conn = null;
        try {
            conn = (FileConnection) Connector.open(path, Connector.READ_WRITE);
            if (!conn.exists()) {
                conn.mkdir();
            }
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }
    
    private void cachePut (String url, CacheItem cacheItem) throws IOException{
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	DataOutputStream dos = new DataOutputStream(os);
    	
    	dos.writeUTF(cacheItem.getUrl());
    	dos.writeLong(cacheItem.getExpires());
    	dos.writeInt(cacheItem.getData().length);
    	dos.write(cacheItem.getData());
    	cacheItem.getHttpHeaders().writeToStream(dos);
    	dos.flush();
    	
    	FileConnection conn = null;
        OutputStream resultOs = null;
        String md5HashFileName = Utils.getMD5Hash(url);
        try {
        	conn = (FileConnection) Connector.open(getCachepath() + md5HashFileName, Connector.READ_WRITE);
            if (!conn.exists()) {
                conn.create();
            }
            resultOs = conn.openOutputStream();
            resultOs.write(os.toByteArray());
        } finally {
            if (null != resultOs) {
                resultOs.close();
            }
        	if (null != conn) {
                conn.close();
            }
            if (null != dos) {
            	dos.close();
            }
            if (null != os) {
            	os.close();
            }
        }
    }
    
    
    /**
     * Get item from cache
     *
     * @param url of browser resource
     * @return CacheItem
     */
    private CacheItem cacheGet(String url) throws IOException{
    	if (null != lastSavedCacheItem) {
    		if (lastSavedCacheItem.getUrl().equals(url)) return lastSavedCacheItem;
    	}
    	String md5HashFileName = Utils.getMD5Hash(url);
    	FileConnection conn = (FileConnection) Connector.open(getCachepath() + md5HashFileName, Connector.READ);
        if (!conn.exists()) {
            return null;
        }

        int size = (int) conn.fileSize();
        byte[] data = new byte[size];
        DataInputStream dis = conn.openDataInputStream();
        dis.readFully(data);
        dis.close();
        conn.close();
        
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));

 //    	Read data from stream
    	String  _url = is.readUTF();
 
    	long _expires = is.readLong();

    	int dataSize = is.readInt();
    	byte[] _data = new byte[dataSize];
    	is.readFully(_data);

    	HttpHeaders _httpHeaders = new HttpHeaders();
    	_httpHeaders.readFromStream(is);
    	is.close();
    	lastSavedCacheItem = new CacheItem(_url, _expires, _data, _httpHeaders);
    	return lastSavedCacheItem;
    }

    /**
     * Check item in cache directory
     *
     * @param url of browser resource
     * @return true if item present
     */
    private boolean cacheContains (String url) {
    	FileConnection conn = null;
    	String md5HashFileName = Utils.getMD5Hash(url);
    	try {
    		conn = (FileConnection) Connector.open(getCachepath() + md5HashFileName, Connector.READ_WRITE);
    		if (conn.exists()) {
                return true;
            }
    	} catch (Exception e) {
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
    	return false;
    }

    /**
     * Remove cache item
     *
     * @param url of browser resource
     */
    private void cacheRemove(String url) {
    	FileConnection conn = null;
    	String md5HashFileName = Utils.getMD5Hash(url);
    	try {
    		conn = (FileConnection) Connector.open(getCachepath() + md5HashFileName, Connector.READ_WRITE);
    		if (conn.exists()) {
        		conn.delete();
    		}
    		
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
    }
}
