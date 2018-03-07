import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProtocolCreator {
	
	
	private String p2p;
	private String cs;
	private String length;
	private String up;
	private String down;
	private byte[] reply;
	public ProtocolCreator(int p2p, int cs, int length, String data, byte[] reply)
	{
		
		this.p2p = Integer.toString(p2p);
		this.cs = Integer.toString(cs);
		this.length = Integer.toString(length);
		this.up = Integer.toString(setUpDown(data,"Up"));
		this.down = Integer.toString(setUpDown(data,"Down"));
		this.reply = reply;
		setProtocol();	
	}
	
	private byte[] intToBytes(int integer)
	{
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) {
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
			outputStream.write( down.getBytes() );
			outputStream.write( reply );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte finalReply[] = outputStream.toByteArray( );
		String s = new String(finalReply);
		
		System.out.println(s);
		System.out.println();
	}
	
	private byte [] getProtocol()
	{
		return cs.getBytes();
	}
	
}
