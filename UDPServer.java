

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class UDPServer 
{
    DatagramSocket socket = null;

    public UDPServer() 
    {

    }
    private static class Hmap{
//    	public HashMap<InetAddress, String> currentHmap = new HashMap<InetAddress, String>();
    	public HashMap<Integer, String> currentHmap = new HashMap<Integer, String>();
    }
    public void createAndListenSocket() throws InterruptedException 
    {
    	final Hmap hmap = new Hmap();
    	
    	Thread clearHmap = new Thread() {
    		@Override
    		public void run() {
    				
    			try {
    				while (true)
    				{
    					Thread.sleep(30000);
    					System.out.println(hmap.currentHmap.entrySet());
    					System.out.println("Reset all hashmap values");
	    				hmap.currentHmap.replaceAll((k, v) -> "Not Available");
    				}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	};
    	
    	try 
        {
            socket = new DatagramSocket(9876);
            byte[] incomingData = new byte[1024];
            
            clearHmap.start();  //Sets all known clients to N/A and starts 30s wait
            while (true)
            {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, 
                		incomingData.length);
                socket.receive(incomingPacket);
                String message = new String(incomingPacket.getData());
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
            	hmap.currentHmap.put(port, "Available");
                
                System.out.println("Received message from client: " + message);
                System.out.println("Client IP:"+IPAddress.getHostAddress());
                System.out.println("Client port:"+port);
                String reply = hmap.currentHmap.entrySet().toString();
                byte[] data = reply.getBytes();
                
                DatagramPacket replyPacket =
                        new DatagramPacket(data, data.length, IPAddress, port);
                
                socket.send(replyPacket);
            }
        }
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException i) 
        {
            i.printStackTrace();
        }
    	finally
    	{
    		socket.close();
    	}
    }

    public static void main(String[] args) throws InterruptedException 
    {
        UDPServer server = new UDPServer();
        server.createAndListenSocket();
    }
}
