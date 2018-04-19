package Project2;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.*;

public class ProxyThread extends Thread
{
   private Socket socket = null;
   private static final int BUFFER_SIZE = 32768;
   public DataOutputStream proxyToClient;
   public BufferedReader clientToProxy;
   
   public ProxyThread(Socket socket)
   {
      this.socket = socket;
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
         
         // If cached do this...
         //sendCachedString("example");
         //Else...
         sendServerString(urlToCall);
         
         
         // Send client request to the server
         BufferedReader rd = null;
         InputStream is = null;
         try
         {
            URL url = new URL(urlToCall);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            String request = "";
            
            // Don't know how this works
            if (conn.getContentLength() > 0)
            {
               is = conn.getInputStream();
               rd = new BufferedReader(new InputStreamReader(is));
               while (rd.readLine() != null)
               {

                  request += rd.read();

               }
            }

            // Send response to client
            byte by[] = new byte[BUFFER_SIZE];
            int index = is.read(by, 0, BUFFER_SIZE);

            while (index != -1)
            {
            	proxyToClient.write(by, 0, index);
            	proxyToClient.write(by, 0, index);
            	index = is.read(by, 0, BUFFER_SIZE);
            }

            proxyToClient.flush();
         }
         catch (Exception e)
         {
            System.err.println("Encountered exception: " + e);
            proxyToClient.writeBytes("");
         }

         // close out all resources
         if (proxyToClient != null)
         {
        	 proxyToClient.close();
         }
         if (clientToProxy != null)
         {
        	 clientToProxy.close();
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
      
   private void sendCachedString(String urlToCall)
   {
	   
   }
   
   private void sendServerString(String urlToCall)
   {
	// Send client request to the server
       InputStream is = null;
       try
       {
          URL url = new URL(urlToCall);
          URLConnection conn = url.openConnection();
          conn.setDoInput(true);
          conn.setDoOutput(false);

          //Don't know how this works
          if (conn.getContentLength() > 0)
          {
            is = conn.getInputStream();
          }

          
          // Send response to client
          byte by[] = new byte[BUFFER_SIZE];
          int index = is.read(by, 0, BUFFER_SIZE);
          
          while (index != -1)
          {
        	  proxyToClient.write(by, 0, index);
             index = is.read(by, 0, BUFFER_SIZE);
          }
          proxyToClient.flush();
       }
       catch (Exception e)
       {
          System.err.println("Encountered exception: " + e);
          try{
        	  proxyToClient.writeBytes("");
          	} catch (IOException e1) {}
       }
   }
}
