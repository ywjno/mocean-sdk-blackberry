package com.adserver.core;

import net.rim.device.api.crypto.MD5Digest;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.
 */
public class CacheManager {

    public static final String ADSERVER_ROOT = "Adserver/"; // Main workspace name
    public static final String ADSERVER_CACHE = "cache/";   // subdir for cache
    private static CacheManager instance;

    private String rootpath = "";   // Path to main workspace


    /**
     * Constructor
     */
    private CacheManager() {
        try {
            rootpath = findRootpath();
        } catch (IOException e) {
            rootpath = "file:///store/home/user/" + ADSERVER_ROOT;
        }
    }


    public static CacheManager getInstance() {
        if (null == instance) {
            instance = new CacheManager();
        }
        return instance;
    }


    /**
     * Search path to main workspace
     *
     * @return
     * @throws IOException
     */
    private static String findRootpath() throws IOException {
        Enumeration drives = FileSystemRegistry.listRoots();
        String root = "";
        while (drives.hasMoreElements()) {
            root = (String) drives.nextElement();
            if (root.equalsIgnoreCase("SDCard/")) {
                root = "SDCard/";
                break;
            } else if (root.equalsIgnoreCase("store/")) {
                root = "store/home/user/";
                break;
            }
        }

        // TODO debug
//        root = "SDCard/";

        root = "file:///" + root + ADSERVER_ROOT;
        createDirectory(root);
        createDirectory(root + ADSERVER_CACHE);

        return root;
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

    /**
     * Load cache index hashtable (link; link hash)
     *
     * @param path Path to cache hashtable file
     * @return
     * @throws IOException
     */
    public static Hashtable loadCacheIndex(final String path) throws IOException {
        FileConnection conn = (FileConnection) Connector.open(path, Connector.READ);
        if (!conn.exists()) {
            return new Hashtable(0);
        }

        int size = (int) conn.fileSize();
        byte[] data = new byte[size];
        conn.openDataInputStream().readFully(data);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));

        int urlSize;
        byte[] url;
        int hashSize;
        byte[] hash;

        // read URL
        urlSize = is.readInt();
        url = new byte[urlSize];
        if (urlSize != is.read(url)) {
            throw new IOException();
        }

        int count = is.readInt();
        Hashtable result = new Hashtable(count);
        try {
            result.put("lastURL", new String(url, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            result.put("lastURL", "");
        }

        for (int i = 0; i < count; ++i) {
            urlSize = is.readInt();
            url = new byte[urlSize];
            if (urlSize != is.read(url)) {
                throw new IOException();
            }
            hashSize = is.readInt();
            hash = new byte[hashSize];
            if (hashSize != is.read(hash)) {
                throw new IOException();
            }

            result.put(new String(url, "UTF-8"), new String(hash, "UTF-8"));
        }

        return result;
    }

    /**
     * Save cache index hashtable (link; link hash)
     *
     * @param path  Path to cache hashtable file
     * @param index Hashtable
     * @param url   Last URL
     * @throws IOException
     */
    public static void saveCacheIndex(final String path, final Hashtable index, final String url) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(os);

        String str;
        byte[] strBytes;

        // save URL
        strBytes = url.getBytes("UTF-8");
        dos.writeInt(strBytes.length);
        dos.write(strBytes);

        // save items count
        dos.writeInt(index.size());

        for (Enumeration i = index.keys(); i.hasMoreElements();) {
            str = (String) i.nextElement();
            strBytes = str.getBytes("UTF-8");
            dos.writeInt(strBytes.length);
            dos.write(strBytes);

            str = (String) index.get(str);
            strBytes = str.getBytes("UTF-8");
            dos.writeInt(strBytes.length);
            dos.write(strBytes);
        }

        FileConnection conn = null;
        OutputStream resultOs = null;
        try {
            conn = (FileConnection) Connector.open(path, Connector.READ_WRITE);
            if (!conn.exists()) {
                conn.create();
            }
            resultOs = conn.openOutputStream();
            resultOs.write(os.toByteArray());
        } finally {
            if (null != conn) {
                conn.close();
            }
            if (null != resultOs) {
                resultOs.close();
            }
        }
    }


    public String getCachepath() {
        return rootpath + ADSERVER_CACHE;
    }


    public static String getMD5Hash(String hash) {
        MD5Digest digest = new MD5Digest();
        try {
            digest.update(hash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            digest.update(hash.getBytes());
        }
        return byteArrayToHexString(digest.getDigest());
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
}
