import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProtocolCreator {
	
	
	private String p2p;
	private String cs;
	private String length;
	private String up;
	private String down;
	private int version = 1;
	private String procVersion;
	private byte[] reply;
	private byte[] finalReply;
	
	public ProtocolCreator(int p2p, int cs, int length, String data, byte[] reply)
	{
	
		this.p2p = Integer.toString(p2p);
		this.cs = Integer.toString(cs);
		this.length = padZero(length, 7);
		this.up = padZero((setUpDown(data,"Up")),4);
		this.down = padZero((setUpDown(data,"Down")),4);
		procVersion = padZero(version,2);
		this.reply = reply;
		setProtocol();	
	}
	private String padZero(int num, int padAmount)
	{
		String s = Integer.toBinaryString(num);
		
		if(s.length() < padAmount)
		{
			for(int x = s.length(); x < padAmount; x++)
			{
				s = "0" + s;
			}
		}
		return s;
		
	}
	private byte[] intToBytes(int integer)
	{
		byte[] bytes = new byte[4];
		for (int i = 0; i < 3; i++) {
		    bytes[i] = (byte)(integer >>> (i * 8));
		}
		return bytes;
	}
	
	private int setUpDown(String data, String findStr)
	{
		String str = data;
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

		    lastIndex = str.indexOf(findStr,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += findStr.length();
		    }
		}
		return count;
		
	}

	
	private void setProtocol()
	{
		
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		try {
			outputStream.write( p2p.getBytes() );
			outputStream.write( cs.getBytes() );
			outputStream.write( length.getBytes() );
			outputStream.write( up.getBytes() );
			outputStream.write( down.getBytes());
			outputStream.write( procVersion.getBytes());
			outputStream.write( reply );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte finalReply[] = outputStream.toByteArray( );
		this.finalReply = finalReply;
		String s = new String(finalReply);
		System.out.println(s);
	}
	
	 byte [] getProtocol()
	{
		return finalReply;
	 }
	
}
