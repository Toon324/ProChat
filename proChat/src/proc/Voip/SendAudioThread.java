package proc.Voip;

import java.io.BufferedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.TargetDataLine;
import javax.swing.JTextArea;

public class SendAudioThread extends Thread implements Runnable {

	private TargetDataLine targetDataLine;
	private byte[] buffer;
	private BufferedOutputStream objectOutputStream;
	JTextArea text;
	DatagramSocket sock;

	public SendAudioThread(byte[] buff, BufferedOutputStream output,
			TargetDataLine data, JTextArea t) {
		buffer = buff;
		objectOutputStream = output;
		targetDataLine = data;
		text = t;
		try {
			sock = new DatagramSocket();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		// try {
		// Boolean running = true;
		// while (running) {
		// int count = targetDataLine.read(buffer, 0, buffer.length);
		// if (count > 0) {
		// /*
		// * SpeexEncoder encoder = new SpeexEncoder();
		// * encoder.init(1, SpeexEncoding.DEFAULT_QUALITY, 16000, 1);
		// *
		// * encoder.processData(buffer, 0, buffer.length); byte[]
		// * encoded = new byte[encoder.getProcessedDataByteSize()];
		// * encoder.getProcessedData(encoded, 0);
		// */
		// /*
		// * byte[] buffer2 = new byte[5]; ShortBuffer intBuf =
		// * ByteBuffer.wrap(buffer) .order(ByteOrder.LITTLE_ENDIAN)
		// * .asShortBuffer(); short[] samples16Bit = new short[intBuf
		// * .remaining()]; intBuf.get(samples16Bit); buffer2 = new
		// * byte[samples16Bit.length]; for (int i = 0; i <
		// * samples16Bit.length; i++) { buffer2[i] = (byte)
		// * ((samples16Bit[i] / 256) + 128); }
		// */
		//
		// text.setText(buffer.length + "   " + buffer[0] + "   "
		// + buffer[1] + "   " + buffer[2] + "   " + buffer[3]);
		// /*
		// * text.append("\n" + buffer2.length + "   " + buffer2[0] +
		// * "   " + buffer2[1] + "   " + buffer2[2] + "   " +
		// * buffer2[3]);
		// */
		//
		// objectOutputStream.write(buffer, 0, buffer.length);
		// /*
		// * objectOutputStream.write(encoded, 0, encoded.length);
		// * //Log.l("Wrote: " + buffer[0] + "," + buffer[1] + "," +
		// * buffer[2]); /* InputStream input = new
		// * ByteArrayInputStream(buffer); final AudioInputStream ais
		// * = new AudioInputStream( input, audioFormat, buffer.length
		// * / audioFormat.getFrameSize());
		// */
		//
		// }
		// }
		//
		// objectOutputStream.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		byte[] testData = new byte[10];
		for (int x = 0; x < testData.length; x++) {
			testData[x] = (byte) x;
			// text.append("  " + (byte) x);
		}

		try {
			DatagramPacket toSend = new DatagramPacket(testData,
					testData.length, InetAddress.getByName("129.89.185.120"),
					1324);

			toSend.setData(testData);
			sock.send(toSend);

			text.append("\nSent.");

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
