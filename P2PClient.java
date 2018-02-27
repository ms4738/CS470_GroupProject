

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class P2PClient 
{
    DatagramSocket Socket;

    public P2PClient() 
    {

    }

    public void createAndListenSocket() throws InterruptedException 
    {
        try 
        {
            Socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] incomingData = new byte[1024];
            String sentence = "Hi";
            byte[] data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            
            Random rand = new Random();
            int randtime;
            while (true)
            {
            	randtime = rand.nextInt(31);
	            Socket.send(sendPacket);
	            System.out.println("Message sent from client");
	            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
	            Socket.receive(incomingPacket);
	            String response = new String(incomingPacket.getData());
	            System.out.println("Response from server:" + response);
	            System.out.println("Waiting for: " + randtime + "s");
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

    public static void main(String[] args) throws InterruptedException 
    {
        P2PClient client = new P2PClient();
        client.createAndListenSocket();
    }
}
