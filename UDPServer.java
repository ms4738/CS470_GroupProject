

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UDPServer 
{
    DatagramSocket socket = null;
    final ConcurrentMap<Integer, Integer> timeHmap = new ConcurrentHashMap<>();
    String reply;
    
    public UDPServer() 
    {

    }
    
    public void createAndListenSocket() throws InterruptedException 
    {
    	try 
        {
            socket = new DatagramSocket(9876);
            byte[] incomingData = new byte[512];
            
            while (true)
            {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, 
                		incomingData.length);
                socket.receive(incomingPacket);
                String message = new String(incomingPacket.getData());
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                int currentTime = (int)System.currentTimeMillis()/1000;
                timeHmap.put(port, currentTime);
                
                System.out.println("Received message from client: " + message);
                System.out.println("Client IP:"+IPAddress.getHostAddress());
                System.out.println("Client port:"+port);
                adjustHashMap(port, currentTime);
                //String reply = timeHmap.entrySet().toString();
                reply = "[";
                timeHmap.forEach((key, updateTime) -> reply += key + " = " + (currentTime > updateTime + 30 ? "Down, " : "Up, "));
                reply += "]";
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

    public void adjustHashMap(int port, int currentTime) 
	{
		System.out.println("Reset expired hashmap values");
		timeHmap.put(port, currentTime);
//		timeHmap.replaceAll((k, v) -> v + 30 > currentTime ? v : 0);
	}
    
    public static void main(String[] args) throws InterruptedException 
    {
        UDPServer server = new UDPServer();
        server.createAndListenSocket();
    }
}
