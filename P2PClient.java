
import java.io.IOException;
import java.net.*;


public class P2PClient
{
   DatagramSocket Socket;

   public P2PClient()
   {
      ClientComm clientComm = new ClientComm();
      clientComm.start();
   }

   public static void main(String[] args) throws InterruptedException, IOException
   {
      P2PClient client = new P2PClient();
   }
}
