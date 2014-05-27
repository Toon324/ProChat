package proc.Voip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Cody
 *
 */
public class RelayServer {

	   public static void main(String[] args) {
	        RelayServer rs = new RelayServer();
	        try {
	             rs.startServer();
	        } catch (IOException e) {
	             e.printStackTrace();
	        } catch (InterruptedException e) {
	             e.printStackTrace();
	        }
	   }

	private boolean shouldRun = true;

	   private void startServer() throws IOException, InterruptedException {
	        DatagramSocket serverSocket = new DatagramSocket(1324);
	        byte[] receiveData = new byte[50];
	      
	        while (shouldRun) {
	             DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	             System.out.println("Listening...");
	             serverSocket.receive(receivePacket);
	             String in = new String(receivePacket.getData());
	             String ipIn = in.substring(0, in.indexOf(" "));
	             String ipOut = in.substring(in.indexOf(" "), in.length());
	             System.out.println("From: " + ipIn + " To: " + ipOut);
	        }
	        serverSocket.close();
	   }
	 
//	   private synchronized void sendPacket(DatagramSocket socket) throws IOException{
//	      
//	        byte[] sendData = new byte[50];
//	        ///sendData = data.getBytes();
//	        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, homeIPAddress, homePort);
//	        socket.send(sendPacket);
//	   }
	 
	}
