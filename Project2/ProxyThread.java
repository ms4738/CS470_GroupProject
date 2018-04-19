package Project2;

import java.net.*;
import java.io.*;
import java.util.*;

public class ProxyThread extends Thread
{
   private Socket socket = null;
   private static final int BUFFER_SIZE = 32768;
   
   public DataOutputStream proxyToClient;
   public BufferedReader clientToProxy;
   public InputStream serverToProxy;
   public boolean isCached;
   
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
         
         
         //Methods that get run
         if (ProxyServer.cache.get(urlToCall) == null)
         {
        	 isCached = false;
        	 cacheServerString(urlToCall);
         }
         sendCachedString(ProxyServer.cache.get(urlToCall));

         
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
   
   //TODO Turn in a PDF with source code, description of design, contributions
   //any other relevant information
   
   //TODO Still needs a thread to periodically checks If-Modified-Since and update if so.
   //Will need to worry about locking which was addressed in Project 1
   private void cacheServerString(String urlToCall)
   {
	   // Send client request to the server
       try
       {
          URL url = new URL(urlToCall);
          URLConnection conn = url.openConnection();
          conn.setDoInput(true);
          conn.setDoOutput(false);

          if (conn.getContentLength() > 0)
          {
        	  serverToProxy = conn.getInputStream();
          }
       }
       catch (Exception e)
       {
          System.err.println("Encountered exception: " + e);
       }
       
       // Save response to hashmap
       try
       {
          byte[] bytes = serverToProxy.readAllBytes();
          ProxyServer.cache.put(urlToCall, bytes.clone());
       }
       catch (Exception e)
       {
          System.err.println("Encountered exception: " + e);
       }
   }
   
   //TODO Still need to also send 304, 400, 501 response codes
   private void sendCachedString(byte[] bytes)
   {
       // Send response to client
       try
       {
          int index = bytes.length;
          
    	  proxyToClient.write(bytes, 0, index);
          proxyToClient.flush();
       }
       catch (Exception e)
       {
          System.err.println("Encountered exception: " + e);
       }
   }
}
