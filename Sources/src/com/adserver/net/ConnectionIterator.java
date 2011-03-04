package com.adserver.net;

import javax.microedition.io.HttpConnection;

public interface ConnectionIterator {
    public HttpConnection getNextConnection() throws NoMoreTransportsException;
}
