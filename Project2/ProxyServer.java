package Project2;

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

class WebsiteInfo
{
	private byte[] websiteBody;
	private Date lastModified;
	private int timeRetreived; 
	private int statusCode;

	public void setBody(byte[] bytes)
	{
		websiteBody = bytes;
	}
	public void setLastModified(Date time)
	{
		lastModified = time;
	}
	public void setTimeRetreived(int retreived)
	{
		timeRetreived = retreived;
	}
	public void setStatusCode(int code)
	{
		statusCode = code;
	}
	public byte[] getBody()
	{
		return websiteBody;
	}
	public Date getLastModified()
	{
		return lastModified;
	}
	public int getTimeRetreived()
	{
		return timeRetreived;
	}
	public int getStatusCode()
	{
		return statusCode;
	}
	public void printWebInfo() throws UnsupportedEncodingException
	{
		System.out.println("Body: " + new String(this.getBody(), "UTF-8"));
		System.out.println("TimeRetreived: " + this.getTimeRetreived());
		System.out.println("LastModifed: " + this.getLastModified());
		System.out.println("StatusCode: " + this.getStatusCode() + "\n");
	}
}

public class ProxyServer extends Thread
{
	static ConcurrentMap<String, WebsiteInfo> cache;

	/**
	 * Refreshes the cache every 30 seconds to make sure that 
	 * recent modifications of the websites have been updated in the cache
	 */
	public void run()
    {
		int deleteThreshold = 30;
    	while (true)
    	{
    		try {
				TimeUnit.SECONDS.sleep(deleteThreshold + 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		//Delete unused websites
    		cache.forEach((siteAddress, websiteInfo) ->
    		{
    			if (websiteInfo.getTimeRetreived() + deleteThreshold < (int)System.currentTimeMillis()/1000)
    			{
    				System.out.println("Site has been removed");
    				cache.remove(siteAddress);
    			}
    		});
    		
    		cache.forEach((siteAddress, websiteInfo) ->
	        {
				System.out.println("SiteAddress: " + siteAddress);
//				System.out.println("Body: " + websiteInfo.getBody());
				System.out.println("TimeRetreived: " + websiteInfo.getTimeRetreived());
				System.out.println("LastModifed: " + websiteInfo.getLastModified());
				System.out.println("StatusCode: " + websiteInfo.getStatusCode() + "\n");
	        });
    	}
    }

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException
	{
		ServerSocket serverSocket = null;
		cache = new ConcurrentHashMap<>();

		ProxyServer server = new ProxyServer();
		server.start();

		int port = 10000; // default
		try
		{
			port = Integer.parseInt(args[0]);
		}
		catch (Exception e)
		{
		}

		try
		{
			serverSocket = new ServerSocket(port);
			System.out.println("Started on: " + port);
		}
		catch (IOException e)
		{
			System.err.println("Could not listen on port: " + args[0]);
			System.exit(-1);
		}

		while (true)
		{
			new ProxyThread(serverSocket.accept()).start();
		}
	}
}
