package proc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AudioCapture extends JFrame implements ActionListener {

	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	String IP = "";
	JTextArea text;
	JFrame frame;
	VoiceCall call;

	public AudioCapture(String ip, VoiceCall voiceCall) {// constructor
		call = voiceCall;
		IP = ip;
		frame = new JFrame();
		text = new JTextArea();

		JScrollPane scroller = new JScrollPane(text);
		scroller.setAutoscrolls(true);

		JButton start = new JButton("Start");
		start.addActionListener(this);

		frame.add(scroller);
		frame.add(start, BorderLayout.SOUTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(400, 400);
		frame.setLocation(300, 300);
		frame.setVisible(true);
		frame.setTitle("Input info");
		frame.requestFocus();

	}// end constructor

	// This method captures audio input
	// from a microphone and saves it in
	// a ByteArrayOutputStream object.
	public void captureAudio() {
		try {
			// Get everything set up for
			// capture
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();

			frame.requestFocus();

			/*
			 * int bufferSize = (int) audioFormat.getSampleRate()
			 * audioFormat.getFrameSize(); final byte buffer[] = new
			 * byte[bufferSize];
			 */
			final byte[] buffer = new byte[call.bufferSize];
			Socket socket = null;

			try {
				socket = new Socket(IP, 20);
				Log.l("Created");
				// socket = new Socket("127.0.0.1", 20);
				Log.l("Socket created");
			} catch (Exception e) {
				e.printStackTrace();
			}

			final BufferedOutputStream objectOutputStream = new BufferedOutputStream(
					socket.getOutputStream());

			Runnable runner = new Runnable() {

				public void run() {
					try {
						Boolean running = true;
						while (running) {
							int count = targetDataLine.read(buffer, 0,
									buffer.length);
							if (count > 0) {
								/*
								 * SpeexEncoder encoder = new SpeexEncoder();
								 * encoder.init(1,
								 * SpeexEncoding.DEFAULT_QUALITY, 16000, 1);
								 * 
								 * encoder.processData(buffer, 0,
								 * buffer.length); byte[] encoded = new
								 * byte[encoder.getProcessedDataByteSize()];
								 * encoder.getProcessedData(encoded, 0);
								 */
								byte[] buffer2 = new byte[5];
								ShortBuffer intBuf = ByteBuffer.wrap(buffer)
										.order(ByteOrder.LITTLE_ENDIAN)
										.asShortBuffer();
								short[] samples16Bit = new short[intBuf
										.remaining()];
								intBuf.get(samples16Bit);
								buffer2 = new byte[samples16Bit.length];
								for (int i = 0; i < samples16Bit.length; i++) {
									buffer2[i] = (byte) ((samples16Bit[i] / 256) + 128);
								}

								text.setText(buffer.length + "   " + buffer[0]
										+ "   " + buffer[1] + "   " + buffer[2]
										+ "   " + buffer[3]);
								text.append("\n" + buffer2.length + "   "
										+ buffer2[0] + "   " + buffer2[1]
										+ "   " + buffer2[2] + "   "
										+ buffer2[3]);

								objectOutputStream.write(buffer2, 0,
										buffer2.length);
								/*
								 * objectOutputStream.write(encoded, 0,
								 * encoded.length); //Log.l("Wrote: " +
								 * buffer[0] + "," + buffer[1] + "," +
								 * buffer[2]); /* InputStream input = new
								 * ByteArrayInputStream(buffer); final
								 * AudioInputStream ais = new AudioInputStream(
								 * input, audioFormat, buffer.length /
								 * audioFormat.getFrameSize());
								 */

							}
						}

						objectOutputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			};

			// Create a thread to capture the
			// microphone data and start it
			// running. It will run until
			// the Stop button is clicked.
			call.getPool().execute(runner);
			/*
			 * Thread captureThread = new Thread(runner); captureThread.start();
			 */
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}

		// end catch
	}// end captureAudio method

	// This method creates and returns an
	// AudioFormat object for a given set
	// of format parameters. If these
	// parameters don't work well for
	// you, try some of the other
	// allowable parameter values, which
	// are shown in comments following
	// the declarations.
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
		// ===================================//

	// Inner class to capture data from
	// microphone
	class CaptureThread extends Thread {
		// An arbitrary-size temporary holding
		// buffer
		byte tempBuffer[] = new byte[call.bufferSize];

		public void run() {
			byteArrayOutputStream = new ByteArrayOutputStream();
			stopCapture = false;
			try {// Loop until stopCapture is set
					// by another thread that
					// services the Stop button.
				while (!stopCapture) {
					// Read data from the internal
					// buffer of the data line.
					int cnt = targetDataLine.read(tempBuffer, 0,
							tempBuffer.length);
					if (cnt > 0) {
						// Save data in output stream
						// object.
						byteArrayOutputStream.write(tempBuffer, 0, cnt);
					}// end if
				}// end while
				byteArrayOutputStream.close();
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}// end catch
		}// end run
	}// end inner class CaptureThread
		// ===================================//

	// Inner class to play back the data
	// that was saved.

	class PlayThread extends Thread {
		byte tempBuffer[] = new byte[call.bufferSize];

		public void run() {
			try {
				int cnt;
				// Keep looping until the input
				// read method returns -1 for
				// empty stream.
				while ((cnt = audioInputStream.read(tempBuffer, 0,
						tempBuffer.length)) != -1) {
					if (cnt > 0) {
						// Write data to the internal
						// buffer of the data line
						// where it will be delivered
						// to the speaker.
						sourceDataLine.write(tempBuffer, 0, cnt);
					}// end if
				}// end while
					// Block and wait for internal
					// buffer of the data line to
					// empty.
				sourceDataLine.drain();
				sourceDataLine.close();
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}// end catch
		}// end run
	}// end inner class PlayThread
		// ===================================//

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Start"))
			captureAudio();

	}

}// end outer class AudioCapture01.java
