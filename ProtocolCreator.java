
public class ProtocolCreator {
	
	private byte [] protocol;
	private byte p2p;
	private byte cs;
	private byte length;
	private byte up;
	private byte down;
	private byte[] reply;
	public ProtocolCreator(byte p2p, byte cs, int length, String data, String reply)
	{
		this.p2p = p2p;
		this.cs = cs;
		this.length = (byte)length;
		setUpDown(data);
		this.reply = reply.getBytes();
		setProtocol();	
	}
	
	private void setUpDown(String data)
	{
		String str = data;
		String findStr = "Up";
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

		    lastIndex = str.indexOf(findStr,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += findStr.length();
		    }
		}
		up = (byte) count;
		findStr = "Down";
		count = 0;
		lastIndex = 0;
		while(lastIndex != -1){

		    lastIndex = str.indexOf(findStr,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += findStr.length();
		    }
		}
	}

	
	private void setProtocol()
	{
		protocol[0] = p2p;
		protocol[1] = cs;
		protocol[2] = length;
		protocol[3] = up;
		protocol[4] = down;
		protocol[5] = reply;
	}
	private byte [] getProtocol()
	{
		return protocol;
	}
}
