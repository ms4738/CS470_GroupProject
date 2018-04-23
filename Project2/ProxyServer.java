package Project2;

import java.net.*;
import java.text.SimpleDateFormat;
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
		int refresh = 30;
		while (true)
		{
			try 
			{
				TimeUnit.SECONDS.sleep(refresh + 1);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}

			//Refresh cache for modified web urls
			cache.forEach((siteAddress, websiteInfo) ->
			{
				InputStream serverToProxy = null;
				try
				{
					SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
					URL connection = new URL(siteAddress);
					HttpURLConnection conn = (HttpURLConnection) connection.openConnection();
					String holdStr = conn.getHeaderField("Last-Modified");
					Date d = format.parse(holdStr);
					long cachedMillis = websiteInfo.getLastModified().getTime();
					long websiteMillis = d.getTime();
					//this happens if modified time is more recent
					if(cachedMillis <= websiteMillis)
					{
						websiteInfo.setStatusCode(conn.getResponseCode());
						websiteInfo.setTimeRetreived((int)System.currentTimeMillis()/1000);
						websiteInfo.setLastModified(d);
						if (conn.getContentLength() > 0)
						{
							serverToProxy = conn.getInputStream();
						}


						try
						{

							ByteArrayOutputStream buffer = new ByteArrayOutputStream();
							int nRead;
							byte[] data = new byte[1024];
							while ((nRead = serverToProxy.read(data, 0, data.length)) != -1) {
								buffer.write(data, 0, nRead);
							}
							buffer.flush();
							byte[] byteArray = buffer.toByteArray();
							websiteInfo.setBody(byteArray);
						}
						catch (Exception e)
						{
							System.err.println("Encountered exception: " + e);
						}
						System.out.println("UPDATED:");
						websiteInfo.printWebInfo();
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

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
