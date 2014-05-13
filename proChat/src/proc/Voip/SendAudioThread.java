package proc.Voip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JTextArea;

public class SendAudioThread extends Thread implements Runnable {

	JTextArea text;
	DatagramSocket sock;
	boolean shouldSend = true;
	private String recieverIP;

	public SendAudioThread(String iP, JTextArea t) {
		recieverIP = iP;
		text = t;
		try {
			sock = new DatagramSocket();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		signalRelayServer();
	}

	/**
	 * 
	 */
	private void signalRelayServer() {
		byte[] data = new byte[150];
		try {
			//Send out request to talk with IP
			DatagramPacket packet = new DatagramPacket(data, data.length,
					InetAddress.getByName("129.89.185.223"), 1324);
			packet.setData(recieverIP.getBytes());
			sock.send(packet);
			
			//Listen for return IP
			DatagramSocket returnSock = new DatagramSocket(1324);
			returnSock.receive(packet);
			returnSock.close();
			
			System.out.println("IP returned: " + new String(packet.getData()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	public void run() {
//		byte[] data = new byte[VoiceCall.bufferSize];
//
//		try {
//			DatagramPacket toSend = new DatagramPacket(data, data.length,
//					InetAddress.getByName(recieverIP), 1324);
//			DataLine.Info dataLineInfo = new DataLine.Info(
//					TargetDataLine.class, RecieveAudio.getAudioFormat());
//			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem
//					.getLine(dataLineInfo);
//			targetDataLine.open(RecieveAudio.getAudioFormat());
//			targetDataLine.start();
//
//			text.append("\nSending data to " + toSend.getAddress() + ":"
//					+ toSend.getPort());
//			byte[] ack = (Home.getIP() + " " + recieverIP).getBytes();
//			toSend.setData(ack);
//			sock.send(toSend);
//
//			while (shouldSend) {
//				targetDataLine.read(data, 0, data.length);
//				//Log.l("captured data of length " + data.length);
//
//				// data[0] = (byte) 3;
//				// data[1] = (byte) 4;
//				// data[2] = (byte) 5;
//				// data[3] = (byte) 6;
//				// data[4] = (byte) 7;
//				//
//				// toSend.setData(data);
//
//				sock.send(toSend);
//			}
//			text.append("Socket closed.");
//			sock.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	boolean running = true;

	public void close() {
		running = false;
	}
}
