import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientComm extends Thread {
	
	private final String FILENAME = "Team_IP.txt";
	 final ConcurrentMap<InetAddress, Integer> timeHmap = new ConcurrentHashMap<>();
	
	public ClientComm()
	{
		//Creat Socket
		DatagramSocket Socket;
		//Stuff for client communication here
		//Send datagram to all list (with their current list)	
		 try {
			Socket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		 getFile();
	}

     	private void getFile()
     	{
     	     ArrayList<String> allIP= new ArrayList<String>();
    	     //pulling in file name with Team IP's
    	     BufferedReader br = null;
    		try {
    			br = new BufferedReader(new FileReader(FILENAME));
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	     String sCurrentLine;
    	     byte[] incomingData = new byte[1024];
    	     String sentence = "Hi";
    	     byte[] data = sentence.getBytes();
    	 
    	     ArrayList<DatagramPacket> allPackets = new ArrayList<DatagramPacket>();
             //timeHmap.put(port, currentTime);
    	     	try {
					while ((sCurrentLine = br.readLine()) != null) 
					{
						//Creating a Datagram Packets for all IP's in file
						 int currentTime = (int)System.currentTimeMillis()/1000;		
								System.out.println(sCurrentLine);
								InetAddress IPAddress = null;
								try {
									IPAddress = InetAddress.getByName(sCurrentLine);
								} catch (UnknownHostException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								timeHmap.put(IPAddress, currentTime);
							allPackets.add( new DatagramPacket(data, data.length, IPAddress, 9876));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
     		
     	}
     	public void run()
     	{
     		
     	}

}
