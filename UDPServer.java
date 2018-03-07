

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class UDPServer extends Thread
{
    DatagramSocket socket = null;
    final ConcurrentMap<InetAddress, IPInformation> timeHmap = new ConcurrentHashMap<>();
    int currentTime;
    
	private String reply;
    
	public class IPInformation 
	{
		int portNum = 0;
		int updateTime = 0;

		public IPInformation(int port, int updateTime){
		    this.portNum = port;
		    this.updateTime = updateTime;
		    }
	}
    
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
            	int port;
                DatagramPacket incomingPacket = new DatagramPacket(incomingData,
                		incomingData.length);
                socket.receive(incomingPacket);
                String message = new String(incomingPacket.getData());
                InetAddress IPAddress = incomingPacket.getAddress();
                port = incomingPacket.getPort();
                currentTime = (int)System.currentTimeMillis()/1000;

                System.out.println("Received message from client: " + message);
                System.out.println("Client IP:"+IPAddress.getHostAddress());
                System.out.println("Client port:"+port);
                
                //update HashMap values
                adjustHashMap(IPAddress, port, currentTime);
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
    
    public void run()
    {
    	while (true)
    	{
    		try {
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		//Constructs a reply with all k/v pairs
	    	reply = "[";
	        timeHmap.forEach((key, IPInformation) -> reply += key + " = " + (currentTime > IPInformation.updateTime + 30 ? "Down, " : "Up, "));
	        reply += "]";
	        byte[] data = reply.getBytes();
	
	        timeHmap.forEach((IPAddress, IPInformation) ->
	        {
	        	DatagramPacket replyPacket =
		                new DatagramPacket(data, data.length, IPAddress, IPInformation.portNum);
		
		        try {
					socket.send(replyPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        });
    	}
    }

	public void adjustHashMap(InetAddress IPAddress, int port, int currentTime)
	{
		System.out.println("Reset expired hashmap values");
		IPInformation ipInfo = new IPInformation(port, currentTime);
		timeHmap.put(IPAddress, ipInfo);
	}

    public static void main(String[] args) throws InterruptedException
    {
        UDPServer server = new UDPServer();
        server.start();
        server.createAndListenSocket();
    }
}
