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
	byte[] inputData = new byte[VoiceCall.bufferSize];
	DatagramPacket datagram;
	RecieveAudio ra;

	public UDPInThread(RecieveAudio r) {
		ra = r;

		datagram = new DatagramPacket(inputData, inputData.length);
		// RecieveAudio.text.append("\nSocket Created.");
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
					+ ra.socket.getLocalPort() + "\n");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			try {
				// while (!sock.isConnected()) {
				// //nothing
				// }

				ra.socket.receive(datagram);
				RecieveAudio.text.append("\nConnection made.");
				byte[] data = datagram.getData();
				RecieveAudio.text.setText("\n" + data[0] + " " + data[1] + " "
						+ data[2] + " " + data[3] + " " + data[4]);
				// Log.l("waiting...");
				ra.recievePacket(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
