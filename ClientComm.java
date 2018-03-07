import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


public class ClientComm extends Thread {

	private final String FILENAME = "IP_Addresses.txt";
	private Map<InetAddress, Integer> timeHmap;
	private ArrayList<DatagramPacket> allPackets;
	private DatagramSocket socket;
	private String reply;
	ServerComm serverComm;

	public ClientComm()
	{
		 timeHmap = new ConcurrentHashMap<>();
		 serverComm = new ServerComm();
	     serverComm.start();
		//Creat Socket
		//Stuff for client communication here
		//Send datagram to all list (with their current list)	
		try {
			  socket = new DatagramSocket(9876);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		getFile();
	}

	private void getFile()
	{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(FILENAME));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sCurrentLine;    	 
		try {
			while ((sCurrentLine = br.readLine()) != null) 
			{
				//Creating a Datagram Packets for all IP's in file
				int currentTime = (int)System.currentTimeMillis()/1000;		
				InetAddress IPAddress = null;
				try {
					IPAddress = InetAddress.getByName(sCurrentLine);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//	System.out.println("IP" + IPAddress);
				timeHmap.put(IPAddress, currentTime);	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	public void run()
	{
		while(true)
		{
			int currentTime = (int)System.currentTimeMillis()/1000;	
			Random rand = new Random();
			reply = timeHmap.toString();
			//timeHmap.forEach((key, updateTime) -> reply += key + " = " + (currentTime > updateTime + 30 ? "Down, " : "Up, "));
		
			byte[] data = reply.getBytes();
				int randtime = rand.nextInt(31);
				//For every  entry in the map send packet
				for (InetAddress address : timeHmap.keySet())
				{
						//System.out.println("Sent to:" + address);
						try {
							//send a new packet with timehMap data to each entry.getKey(the InetAddress)
							socket.send(new DatagramPacket(data, data.length, address, 9877));
						//	
						} catch (IOException e) {
					//		 TODO Auto-generated catch block
							e.printStackTrace();
					} 

					}
					
					try {
					//	System.out.println("Waiting for: " + randtime + "s");	
						TimeUnit.SECONDS.sleep(randtime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//GRAB NEW DATA (timeHMap)
				timeHmap = serverComm.getTimeHMap();
				
			}
		}
	
	public void printCurrentHMap(ConcurrentMap<InetAddress, Integer> timeHmap)
	{
		int currentTime = (int)System.currentTimeMillis()/1000;	
		reply = "[";
        timeHmap.forEach((key, updateTime) -> reply += key + " = " + (currentTime > updateTime + 30 ? "Down, " : "Up, "));
        reply += "]";
        System.out.println(reply);
	}
}
