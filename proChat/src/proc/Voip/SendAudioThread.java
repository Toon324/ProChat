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
	VoiceCall call;
	
	public SendAudioThread(VoiceCall vc, JTextArea t) {
		call = vc;
		text = t;
		
	}

	public void run() {
		byte[] data = new byte[VoiceCall.bufferSize];

		try {
			
			//Start capturing audio
			DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, RecieveAudio.getAudioFormat());
			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem
					.getLine(dataLineInfo);
			targetDataLine.open(RecieveAudio.getAudioFormat());
			targetDataLine.start();
			

			while (shouldSend) {
				targetDataLine.read(data, 0, data.length);
				//Log.l("captured data of length " + data.length);

				 data[0] = (byte) 3;
				 data[1] = (byte) 4;
				 data[2] = (byte) 5;
				 data[3] = (byte) 6;
				 data[4] = (byte) 7;
				
				 call.sendData(data);
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
