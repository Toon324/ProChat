package proc.Voip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JTextArea;

import proc.Home;

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
	}

	public void run() {
		byte[] data = new byte[VoiceCall.bufferSize];

		try {
			DatagramPacket toSend = new DatagramPacket(data, data.length,
					InetAddress.getByName(recieverIP), 1324);
			DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, RecieveAudio.getAudioFormat());
			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem
					.getLine(dataLineInfo);
			targetDataLine.open(RecieveAudio.getAudioFormat());
			targetDataLine.start();

			text.append("\nSending data to " + toSend.getAddress() + ":"
					+ toSend.getPort());
			byte[] ack = (Home.getIP() + " " + recieverIP).getBytes();
			toSend.setData(ack);
			sock.connect(InetAddress.getByName(recieverIP), 1324);
			sock.send(toSend);

			while (shouldSend) {
				targetDataLine.read(data, 0, data.length);

				toSend.setData(data);
				sock.send(toSend);
			}

			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean running = true;

	public void close() {
		running = false;
	}
}
