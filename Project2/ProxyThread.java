package Project2;

import java.net.*;
import java.io.*;
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
               System.out.println(inputLine);
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
        	 cachedWebsite = new WebsiteInfo();
        	 isCached = false;
        	 cacheServerString(urlToCall);
         }
         //TODO Check website for If-Modified-Since IF our predetermined threshhold is passed
         //when client request from server. (THERE IS NO POLLING IN THE SERVER, CLIENT MUST ASK FIRST)
         //If website has a different If-Modified-Since, then get that and update the Hashmap
         else if (cachedWebsite.getTimeRetreived() + 10 < (int)System.currentTimeMillis()/1000)
         {
        	 cacheServerString(urlToCall);
         }
         else
         {
        	 cachedWebsite.setStatusCode(304);
        	 ProxyServer.cache.put(urlToCall, cachedWebsite);
         }
    	 sendCachedString(cachedWebsite.getBody());
         cachedWebsite = ProxyServer.cache.get(urlToCall);
         
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
      catch (IOException e)
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
          
          //This outputs the http header information
//          Map<String, List<String>> map = conn.getHeaderFields();
//          for (Map.Entry<String, List<String>> entry : map.entrySet())
//          {
//        	  System.out.println("Key : " + entry.getKey()
//        			  + " -> value : " + entry.getValue());
//          }
          
          //Formats the date string into a Date
          SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
          format.setTimeZone(TimeZone.getTimeZone("GMT-6"));
          String holdStr = conn.getHeaderField("Last-Modified");
          Date hold;
          if (holdStr != null)
          {
        	  hold = format.parse(holdStr);
        	  cachedWebsite.setLastModified(hold);
          }
          cachedWebsite.setStatusCode(conn.getResponseCode());
          cachedWebsite.setTimeRetreived((int)System.currentTimeMillis()/1000);
          
          //This gets the body of the request (i.e. the website)
          if (conn.getContentLength() > 0)
          {
        	  serverToProxy = conn.getInputStream();
          }
       }
       catch (Exception e)
       {
    	   e.printStackTrace();
       }
       
       // Save response to hashmap
       try
       {
          cachedWebsite.setBody(serverToProxy.readAllBytes());
          ProxyServer.cache.put(urlToCall, cachedWebsite);
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
