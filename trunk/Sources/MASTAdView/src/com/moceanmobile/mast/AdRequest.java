package com.moceanmobile.mast;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.http.HttpHeaders;

public class AdRequest implements Runnable
{
	public interface AdRequestHandler
	{
		void adRequestFailed(AdRequest adRequest, Exception exception);
		void adRequestCompleted(AdRequest adRequest);
	}
	
	private final String url;
	private final String userAgent;
	private AdRequestHandler handler;
	
	private AdResponse adResponse = null;
	
	public AdRequest(String url, String userAgent, AdRequestHandler handler)
	{
		this.url = url;
		this.userAgent = userAgent;
		this.handler = handler;
	}
	
	public void start()
	{
		BackgroundQueue.getInstance().queueTask(this);
	}
	
	public void cancel()
	{
		this.handler = null;
	}
	
	public AdResponse getAdResponse()
	{
		return adResponse;
	}

	public void run()
	{
		Connection connection = null;
		
		try 
		{
			connection = Connector.open(url);
			if (connection instanceof HttpConnection)
			{
				HttpConnection httpConnection = (HttpConnection)connection;
				httpConnection.setRequestMethod(HttpConnection.GET);
				httpConnection.setRequestProperty(HttpHeaders.HEADER_USER_AGENT, userAgent);
				httpConnection.setRequestProperty(HttpHeaders.HEADER_CONTENT_LENGTH, "0");
				
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpConnection.HTTP_OK)
				{
					InputStream inputStream = httpConnection.openInputStream();
					
					/** Debugging, uncomment to output the response stream.
					byte[] buffer = IOUtilities.streamToBytes(inputStream);
					String response = new String(buffer, "UTF-8");
					System.out.println(response);
					inputStream = new ByteArrayInputStream(buffer);
					*/
					
					adResponse = new AdResponse();
					adResponse.parse(inputStream);
					
					if (this.handler != null)
						this.handler.adRequestCompleted(this);
					
					return;
				}
				else
				{
					throw new IOException("Received non-200 response.");
				}
			}
			
			throw new UnsupportedOperationException("SDK only handles HTTP/S connections.");
		}
		catch (Exception e)
		{
			if (this.handler != null)
				this.handler.adRequestFailed(this, e);
		}
		finally
		{
			if (connection != null)
				try { connection.close(); } catch (Exception ex) {}
		}
	}
}
