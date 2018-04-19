package Project2;

import java.net.*;
import java.io.*;
import java.util.HashMap;

public class ProxyServer
{
	static HashMap<String, byte[]> cache;
   
   @SuppressWarnings("resource")
public static void main(String[] args) throws IOException
   {
      ServerSocket serverSocket = null;
      cache = new HashMap<>();
      
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
