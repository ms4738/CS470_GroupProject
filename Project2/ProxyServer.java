package Project2;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ProxyServer extends Thread
{
	static ConcurrentMap<String, byte[]> cache;
   
	//TODO Needs to poll each website for new version
	public void run()
    {
    	while (true)
    	{
    		try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		//Constructs a reply with all k/v pairs
    		cache.forEach((siteAddress, byteInfo) ->
	        {
				System.out.println("test");
	        	//Check If-modified-since
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
