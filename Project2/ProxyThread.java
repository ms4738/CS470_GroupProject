package Project2;

import java.net.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProxyThread extends Thread
{
   private Socket socket = null;
   
   public DataOutputStream proxyToClient;
   public BufferedReader clientToProxy;
   public InputStream serverToProxy;
   public boolean isCached;
   WebsiteInfo cachedWebsite;
   
   public ProxyThread(Socket socket)
   {
      this.socket = socket;
      isCached = true;
   }

   public void run()
   {
      try
      {
         proxyToClient = new DataOutputStream(socket.getOutputStream());
         clientToProxy = new BufferedReader(
        		 new InputStreamReader(socket.getInputStream()));
      System.out.println("Thread start\n");
      String urlToCall = "";

         String inputLine;
         int count = 0;

         // Get request from client
         while ((inputLine = clientToProxy.readLine()) != null)
         {
            try
            {
//               System.out.println(inputLine);
               StringTokenizer tok = new StringTokenizer(inputLine);
               tok.nextToken();
            }
            catch (Exception e)
            {
               break;
            }
            // parse the first line of the request to find the url
            if (count == 0)
            {
               String[] tokens = inputLine.split(" ");
               urlToCall = tokens[1];
            }

            count++;
         }
         
         //Proxy -> Server, Server -> Proxy -> Client
         cachedWebsite = ProxyServer.cache.get(urlToCall);
         if (cachedWebsite == null)
         {
        	 System.out.println("This is a new request");
        	 cachedWebsite = new WebsiteInfo();
        	 isCached = false;
        	 cacheServerString(urlToCall);
         }
         //TODO Check website for If-Modified-Since IF our predetermined threshhold is passed
         //when client request from server. (THERE IS NO POLLING IN THE SERVER, CLIENT MUST ASK FIRST)
         //If website has a different If-Modified-Since, then get that and update the Hashmap
         else if (cachedWebsite.getTimeRetreived() + 10 < (int)System.currentTimeMillis()/1000)
         {
        	SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
       	   	URLConnection connection = new URL(urlToCall).openConnection();
       	   	String holdStr = connection.getHeaderField("Last-Modified");
       	   	Date d = format.parse(holdStr);
       	   	long cachedMillis = cachedWebsite.getLastModified().getTime();
       	   	long websiteMillis = d.getTime();
       	   	//this happens if modified time is more recent
       	   	if(cachedMillis != websiteMillis)
       	   	{
       	   		cacheServerString(urlToCall);
       	   	}
         }
         else
         {
        	 cachedWebsite.setStatusCode(304);
        	 ProxyServer.cache.put(urlToCall, cachedWebsite);
         }
    	 sendCachedString(cachedWebsite.getBody());
         cachedWebsite = ProxyServer.cache.get(urlToCall);
         System.out.println("URL:" + urlToCall);
         cachedWebsite.printWebInfo();
         
         // Cleanup crew
         if (proxyToClient != null)
         {
        	 proxyToClient.close();
         }
         if (clientToProxy != null)
         {
        	 clientToProxy.close();
         }
         if (serverToProxy != null)
         {
        	 serverToProxy.close();
         }
         if (socket != null)
         {
            socket.close();
         }

      }
      catch (IOException | ParseException e)
      {
         e.printStackTrace();
      }
   }
   
   private void cacheServerString(String urlToCall)
   {
	   // Send client request to the server
       try
       {
          URL url = new URL(urlToCall);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setDoInput(true);
          conn.setDoOutput(false);
          
        //Formats the date string into a Date
          SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
          format.setTimeZone(TimeZone.getTimeZone("GMT-6"));
          //We dont needs last modified on the date, just the date that we get it in
          String holdStr = conn.getHeaderField("Last-Modified");
          Date hold;
          if (holdStr != null)
          {
        	  hold = format.parse(holdStr);
        	  cachedWebsite.setLastModified(hold);
//        	  System.out.println(new Date());
          }
          cachedWebsite.setStatusCode(conn.getResponseCode());
          cachedWebsite.setTimeRetreived((int)System.currentTimeMillis()/1000);
          
          switch (conn.getResponseCode())
          {
          	case HttpURLConnection.HTTP_OK:
          	//This gets the body of the request (i.e. the website)
                if (conn.getContentLength() > 0)
                {
              	  serverToProxy = conn.getInputStream();
                }
          		saveResponse(urlToCall);
          		break;
          	case HttpURLConnection.HTTP_BAD_REQUEST:
          		cachedWebsite.setBody(conn.getResponseMessage().getBytes());
          		break;
          	case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
          		cachedWebsite.setBody(conn.getResponseMessage().getBytes());
          		break;
          }
        	  
          
          
          //This outputs the http header information
//          Map<String, List<String>> map = conn.getHeaderFields();
//          for (Map.Entry<String, List<String>> entry : map.entrySet())
//          {
//        	  System.out.println("Key : " + entry.getKey()
//        			  + " -> value : " + entry.getValue());
//          }
          
          
          
          
       }
       catch (Exception e)
       {
    	   e.printStackTrace();
       }
       ProxyServer.cache.put(urlToCall, cachedWebsite);
       
       
   }
  
public void saveResponse(String urlToCall)
{
	// Save response to hashmap
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
 	
 	    //assertThat(text, equalTo(originalString));
       cachedWebsite.setBody(byteArray);
//       System.out.println(byteArray.toString());
       // -----> Actual String Representation   System.out.println(new String(byteArray, "UTF-8"));
      
       //ProxyServer.cacheDate.put(urlToCall, Date);
    }
    catch (Exception e)
    {
       System.err.println("Encountered exception: " + e);
    }
}
   
   //TODO Still need to also send 304, 400, 501 response codes to the client (in Header?)
   private void sendCachedString(byte[] bytes)
   {
       // Send response to client
       try
       {
          int index = bytes.length;
          
    	  proxyToClient.write(bytes, 0, index);
          proxyToClient.flush();

//          This outputs the website onto console
//          proxyToClient = new DataOutputStream(System.out);
//          proxyToClient.write(bytes, 0, index);;
//          proxyToClient.flush();
       }
       catch (Exception e)
       {
          e.printStackTrace();
       }
   }
}
