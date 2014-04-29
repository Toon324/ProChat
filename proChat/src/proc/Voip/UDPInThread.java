package proc.Voip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Cody
 * 
 */
public class UDPInThread implements Runnable {
	DatagramSocket sock;
	byte[] inputData = new byte[VoiceCall.bufferSize];
	DatagramPacket datagram;
	RecieveAudio ra;

	public UDPInThread(RecieveAudio r) {
		ra = r;
		try {
			sock = new DatagramSocket(1324);
			datagram = new DatagramPacket(inputData, inputData.length);
			//RecieveAudio.text.append("\nSocket Created.");
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
		try {
			RecieveAudio.text.append("\nWaiting on packet at "
					+ InetAddress.getLocalHost().getHostAddress() + ":"
					+ sock.getLocalPort() + "\n");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {	
			try {
//				while (!sock.isConnected()) {
//					//nothing
//				}
				
				sock.receive(datagram);
				RecieveAudio.text.append("\nConnection made.");
				byte[] data = datagram.getData();
				RecieveAudio.text.setText("\n" + data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " " +data[4]);

				ra.recievePacket(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
