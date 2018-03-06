

import java.io.IOException;

import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class P2PClient
{
    DatagramSocket Socket;

    public P2PClient() 
    {
    	ClientComm clientComm = new ClientComm();
    	clientComm.start();
    	ServerComm serverComm = new ServerComm();
    	serverComm.start();
    }

    
    /**
     * @throws InterruptedException
     * @throws IOException
     */
    public void createAndListenSocket() throws InterruptedException, IOException 
    {
        try 
        {
            Socket = new DatagramSocket(); 
            ArrayList<String> allIP= new ArrayList<String>();
            //pulling in file name with Team IP's
            final String FILENAME = "/Users/kooapps/Desktop/CS4Life/CSNetworks/bin/Team_IP.txt";
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String sCurrentLine;
            byte[] incomingData = new byte[1024];
            String sentence = "Hi";
            byte[] data = sentence.getBytes();
        
            ArrayList<DatagramPacket> allPackets = new ArrayList<DatagramPacket>();
            	while ((sCurrentLine = br.readLine()) != null) 
            	{
            		//Creating a Datagram Packets for all IP's in file
            				allIP.add(sCurrentLine);
            				System.out.println(sCurrentLine);
            				InetAddress IPAddress = InetAddress.getByName(sCurrentLine);
            			allPackets.add( new DatagramPacket(data, data.length, IPAddress, 9876));
            			            	}            
            	
            Random rand = new Random();
            int randtime;
            while (true)
            {
            	/**
            	
            	*/
            	
            	//Attempting to send a recieve packets for all IP's(may need to be in threads )
            	for(DatagramPacket sendPacket: allPackets )
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
        }
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
         
        }
    

    public static void main(String[] args) throws InterruptedException, IOException 
    {
    
        P2PClient client = new P2PClient();
        client.createAndListenSocket();
    }
}
