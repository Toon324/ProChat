package proc.Voip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Cody
 * 
 */
public class UDPInThread implements Runnable {
	DatagramSocket sock;
	byte[] inputData = new byte[10];
	
	public UDPInThread() {
		try {
			sock = new DatagramSocket(324);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {	
			DatagramPacket datagram = new DatagramPacket(inputData, 0, InetAddress.getByName("129.89.185.223"), 324);
			
			sock.receive(datagram);
			
			for (byte b : datagram.getData())
				RecieveAudio.text.append("   " + b);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
