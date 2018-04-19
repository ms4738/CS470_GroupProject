package Project2;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.*;

public class ProxyThread extends Thread
{
   private Socket           socket      = null;
   private static final int BUFFER_SIZE = 32768;

   public ProxyThread(Socket socket)
   {
      this.socket = socket;
   }

   public void run()
   {
      System.out.println("Thread start\n");
      String urlToCall = "";
      try
      {
         DataOutputStream out = new DataOutputStream(socket.getOutputStream());
         BufferedReader in = new BufferedReader(
               new InputStreamReader(socket.getInputStream()));

         String inputLine;
         int count = 0;

         // Get request from client
         while ((inputLine = in.readLine()) != null)
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
               System.out.print(request);
            }

            // Send response to client
            byte by[] = new byte[BUFFER_SIZE];
            int index = is.read(by, 0, BUFFER_SIZE);

            while (index != -1)
            {
               out.write(by, 0, index);
               out.write(by, 0, index);
               index = is.read(by, 0, BUFFER_SIZE);
            }

            out.flush();
         }
         catch (Exception e)
         {
            System.err.println("Encountered exception: " + e);
            out.writeBytes("");
         }

         // close out all resources
         if (rd != null)
         {
            rd.close();
         }
         if (out != null)
         {
            out.close();
         }
         if (in != null)
         {
            in.close();
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
}