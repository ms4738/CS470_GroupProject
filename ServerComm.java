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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerComm extends Thread
{
	DatagramSocket socket = null;
    //final ConcurrentMap<Integer, Integer> timeHmap = new ConcurrentHashMap<>();
    final ConcurrentMap<InetAddress, Integer> timeHmap = new ConcurrentHashMap<>();
    private String reply;
	
    public ServerComm()
	{
		
	}
	
	
	public void run()
	{
		try 
		{
			socket = new DatagramSocket(9877);
	        byte[] incomingData = new byte[512];
	        
			while(true)
			{
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
	            socket.receive(incomingPacket);
	            InetAddress IPAddressSender = incomingPacket.getAddress();
	            String response = new String(incomingPacket.getData());
	            int currentTime = (int)System.currentTimeMillis()/1000;
	            response = response.substring(response.indexOf("{")+1, response.indexOf("}"));
	            String[] responseString = response.split("[,]+", 5);
	            
	            //Updating the Sender so they are currentTime
	            timeHmap.put(IPAddressSender, currentTime);
	            
	            for (String ipAndTime : responseString)
	            {
	            	String ip = ipAndTime.substring(ipAndTime.indexOf("/")+1,ipAndTime.indexOf("="));	        
                    InetAddress IPAddress = InetAddress.getByName(ip);
//	            	System.out.println("IP " + ip);
	            	int updateTime = Integer.parseInt(ipAndTime.substring(ipAndTime.indexOf("=")+1));
//	            	System.out.println(time);
	            	
	            	if(timeHmap.containsKey(IPAddress))
	            	{
	            		if(timeHmap.get(IPAddress) < updateTime)
	            		{
	            			timeHmap.put(IPAddress, updateTime);
//	            			System.out.print("Updating hash value\n");
	            		}
	            		//Else no changes are needed
	            	}
	            	else
	            	{
	            		timeHmap.put(IPAddress, updateTime);
	            	}
	                      	
//	            	if(IPAddress.toString().equals(incomingPacket.getAddress().toString()))
//	            	{
//	            		timeHmap.put(IPAddress, currentTime);
//	            	}
//	            	else
//	            	{
//	            		if(timeHmap.containsKey(IPAddress))
//	            		{
//	            			if(timeHmap.get(IPAddress) < updateTime)
//	            			{
//		            			timeHmap.put(IPAddress, updateTime);
//		            			System.out.print("Updating hash value");
//	            			} 				
//	            		}
//	            		else
//	            		{
//	            			timeHmap.put(IPAddress, updateTime);
//	            		}
//	            	}
	            	//System.out.println("LastUpdatedTime: " + updateTime + ", TimeFromIP: " + timeHmap.get(IPAddress));
	            }
	            
			}
			
		} 
		catch (SocketException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public ConcurrentMap<InetAddress, Integer> getTimeHMap()
	{
		return timeHmap;
	}

}
