

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UDPClient extends Thread
{
    DatagramSocket Socket;
    
    public UDPClient() 
    {
    	try {
			Socket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
    }

    public void sendMessage() throws InterruptedException 
    {
        try 
        {	
            InetAddress IPAddress = InetAddress.getByName("150.243.215.94");
            
            String sentence = "Hi"; //Some arbitrary message
            byte[] data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            ProtocolCreator p = new ProtocolCreator(0,1,data.length, " ",data);
            	
            Random rand = new Random();
            int randtime;
            while (true)
            {
            	randtime = rand.nextInt(31);
	            Socket.send(sendPacket);
	            System.out.println("Message sent from client");
	            System.out.println("Sending another update in " + randtime + "s");
	            TimeUnit.SECONDS.sleep(randtime);
        	}
        }
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    public void run()
    {
    	byte[] incomingData = new byte[1024];
    	
    	while (true)
    	{
    		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            try {
				Socket.receive(incomingPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
        	String s = new String(incomingPacket.getData());
		    System.out.println("P2P FLAG: " + s.substring(0,1));
            System.out.println("CS FLAG: " + s.substring(1,2));
            System.out.println("LENGTH FLAG: " + s.substring(2,9));
            System.out.println("NODES UP: " + s.substring(9,13));
            System.out.println("NODES DOWN: " + s.substring(13,17));
            System.out.println("VERSION: " + s.substring(17,19));
            System.out.println("Response from server:" + s.substring(19, s.length()-1));
    	}
    }

    public static void main(String[] args) throws InterruptedException 
    {
        UDPClient client = new UDPClient();
        client.start();
        client.sendMessage();
    }
}
