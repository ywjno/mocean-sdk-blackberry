package com.adserver.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class AdserverInputStream extends InputStream {
	private InputStream		is;			// Connection stream

	//  // TODO TEST Disabled cache mode
	//    private OutputStream osCache = null;    // Cache stream

	private String			url;			// Request URL
	private String			urlHash;		// Request path
	private AdserverBase	application;
	private boolean			cacheMode;

	/**
	 * Constructor for network mode
	 * 
	 * @param is
	 * @param url
	 * @param headersLine
	 * @param application
	 */
	// TODO TEST Disabled cache mode
	//    public AdserverInputStream(final InputStream is, final String url, final String headersLine, final AdserverBase application) {
	//        cacheMode = false;
	//
	//        this.is = is;
	//        this.application = application;
	//
	//        this.url = url;
	//        this.urlHash = CacheManager.getMD5Hash(url);
	//
	//        String root = CacheManager.getInstance().getCachepath() + application.getHashId() + '/' + this.urlHash;
	//        FileConnection conn = null;
	//        try {
	//            try {
	//                conn = (FileConnection) Connector.open(root, Connector.READ_WRITE);
	//                if (!conn.exists()) {
	//                    conn.create();
	//                }
	//
	//                 // TODO TEST Disabled cache mode
	////                osCache = conn.openOutputStream();
	////                osCache.write(headersLine.getBytes("UTF-8"));
	//
	//            } finally {
	//                if (null != conn) {
	//                    conn.close();
	//                }
	//            }
	//        } catch (IOException ignored) {
	//        }
	//    }

	/**
	 * Constructor for cache mode
	 * 
	 * @param is
	 * @throws IOException
	 */
	public AdserverInputStream(final InputStream is) throws IOException {
//		byte[] data = IOUtilities.streamToBytes(is);
//		System.out.println("!!!!Response data: " + new String(data));
//		this.is = new ByteArrayInputStream(data);
		
		cacheMode = true;
		this.is = is;
	}

	public int read() throws java.io.IOException {
		int result = is.read();

		// TODO TEST Disabled cache mode
		//        if (!cacheMode) {
		//            osCache.write(result);
		//        }

		return result;
	}

	public int read(byte[] bytes) throws java.io.IOException {
		int result = is.read(bytes);

		// TODO TEST Disabled cache mode
		//        if (!cacheMode) {
		//            osCache.write(bytes, 0, result);
		//        }

		return result;
	}

	public int read(byte[] bytes, int i, int i1) throws java.io.IOException {
		int result = is.read(bytes, i, i1);

		// TODO TEST Disabled cache mode
		//        if (!cacheMode) {
		//            osCache.write(bytes, i, i1);
		//        }

		return result;
	}

	public long skip(long l) throws java.io.IOException {
		return is.skip(l);
	}

	public int available() throws java.io.IOException {
		return is.available();
	}

	public void close() throws java.io.IOException {
		is.close();

		// TODO TEST Disabled cache mode
		//        if (!cacheMode) {
		//            osCache.close();
		//            application.addCacheItem(url, urlHash);
		//        }

	}

	public synchronized void mark(int i) {
		is.mark(i);
	}

	public synchronized void reset() throws java.io.IOException {
		is.reset();
	}

	public boolean markSupported() {
		return is.markSupported();
	}
}
