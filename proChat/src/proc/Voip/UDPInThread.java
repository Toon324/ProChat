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
	byte[] inputData = new byte[1024];
	DatagramPacket datagram;
	RecieveAudio ra;

	public UDPInThread(RecieveAudio r) {
		ra = r;
		try {
			sock = new DatagramSocket(1324);
			datagram = new DatagramPacket(inputData, inputData.length);
			RecieveAudio.text.append("\nSocket Created.");
		} catch (Exception e) {
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
		while (true) {
			try {

				RecieveAudio.text.append("\nWaiting on packet at "
						+ InetAddress.getLocalHost().getHostAddress() + ":"
						+ sock.getLocalPort() + "\n");
				sock.receive(datagram);

				for (byte b : datagram.getData())
					RecieveAudio.text.append(" " + b);

				ra.recievePacket(datagram.getData());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
