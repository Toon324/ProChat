package proc.Voip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JTextArea;

import proc.Home;
import proc.Log;

public class SendAudioThread extends Thread implements Runnable {

	JTextArea text;
	boolean shouldSend = true;
	DatagramSocket comms;
	String recieverIP;
	
	public SendAudioThread(DatagramSocket sock, String reciever, JTextArea t) {
		comms = sock;
		text = t;
		recieverIP = reciever;
		
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
			DatagramPacket toSend = new DatagramPacket(data, data.length);
			System.out.println("Sending to " + recieverIP);
			toSend.setAddress(InetAddress.getByName(recieverIP.substring(0, recieverIP.indexOf(":"))));
			toSend.setPort(Integer.valueOf(recieverIP.substring(recieverIP.indexOf(":") + 1 , recieverIP.length())));
			
			//Start capturing audio
			DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, RecieveAudio.getAudioFormat());
			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem
					.getLine(dataLineInfo);
			targetDataLine.open(RecieveAudio.getAudioFormat());
			targetDataLine.start();

			text.append("\nSending data to " + toSend.getAddress() + ":"
					+ toSend.getPort());

			while (shouldSend) {
				targetDataLine.read(data, 0, data.length);
				//Log.l("captured data of length " + data.length);

				 data[0] = (byte) 3;
				 data[1] = (byte) 4;
				 data[2] = (byte) 5;
				 data[3] = (byte) 6;
				 data[4] = (byte) 7;
				
				 toSend.setData(data);

				comms.send(toSend);
			}
			text.append("Socket closed.");
			comms.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean running = true;

	public void close() {
		running = false;
	}
}
