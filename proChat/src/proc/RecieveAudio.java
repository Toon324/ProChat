package proc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * @author Cody
 * 
 */
public class RecieveAudio {
	ServerSocket server;
	Socket socket;
	JTextArea text;
	VoiceCall call;

	public RecieveAudio(VoiceCall voiceCall) {
		call = voiceCall;
		JFrame frame = new JFrame();
		text = new JTextArea();

		frame.add(text);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(400, 400);
		frame.setLocation(800,200);
		frame.setVisible(true);

		try {
			server = new ServerSocket(20);
			Log.l("Server created");
			text.append("Server created. Hosting at " + Home.getIP());
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	SourceDataLine sline;
	DataLine.Info info;
	final AudioFormat format = getAudioFormat();

	public void playAudio() {
		// try{

		//Log.l("Listening for audio.");
		text.append("\nListening...");
		Runnable runner = new Runnable() {

			public void run() {
				try {
					try {
						socket = server.accept();
						text.append("connection accepted");
						//Log.l("Socket created");
					} catch (Exception e) {
						e.printStackTrace();
					}
					InputStream in = socket.getInputStream();
					Thread playTread = new Thread();
					
					
					
					
					try {
					info = new DataLine.Info(
							SourceDataLine.class, format);
					sline = (SourceDataLine) AudioSystem
							.getLine(info);
					sline.open(format);
					sline.start();
					}
					catch (Exception e) {
						e.printStackTrace();
					}

					int count;
					byte[] buffer = new byte[call.bufferSize];
					
					while (true) {
						count = in.read(buffer, 0, buffer.length);
						if (count == -1 ) {
							text.append("\nEnd of stream detected.");
							break;
						}
						//text.append("\n" + buffer[0] + "  " + buffer[1] + "   " + buffer[2]);
						PlaySentSound(buffer, playTread);
					}
					
				} catch (IOException e) {
					System.err.println("I/O problems:" + e);
				}
			}

		};
		
		

		call.getPool().execute(runner);

		// }
		// catch(LineUnavailableException e) {
		// System.exit(-4);
		// }
	}// End of PlayAudio method

	private void PlaySentSound(final byte buffer[], Thread playThread) {

		synchronized (playThread) {

			Runnable runnable = new Runnable() {

				public void run() {
					try {
						/*
						SpeexDecoder decoder = new SpeexDecoder();
						decoder.init(1, 16000, 1, false);
						decoder.processData(buffer, 0, buffer.length);
						
						byte[] decoded = new byte[decoder.getProcessedDataByteSize()];
						
						decoder.getProcessedData(decoded, 0);

						text.setText(decoded[0] + "   " + decoded[1] + "   " + decoded[2] + "   " + decoded[3]);
						
						InputStream input = new ByteArrayInputStream(decoded);
						*/
						InputStream input = new ByteArrayInputStream(buffer);
						
						final AudioInputStream ais = new AudioInputStream(
								input, format, buffer.length
										/ format.getFrameSize());
						
						text.setText(buffer.length + "   " + buffer[0] + "   " + buffer[1] + "   " + buffer[2] + "   " + buffer[3]);
						
						//Float audioLen = (decoded.length / format.getFrameSize())
						//		* format.getFrameRate();

						int bufferSize = (int) format.getSampleRate()
								* format.getFrameSize();
						byte buffer2[] = new byte[call.bufferSize];
						//int count2;
						
						ais.read(buffer2, 0, buffer2.length);
						sline.write(buffer2, 0, buffer2.length);
						sline.flush();
						sline.drain();
						//sline.stop();
						//sline.close();
						buffer2 = null;

					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			};
			
			call.getPool().execute(runnable);
			/*
			playThread = new Thread(runnable);
			playThread.start();
			*/
		}

	}

	private AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}// end getAudioFormat
}
