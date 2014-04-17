package proc.Voip;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import proc.Home;
import proc.Log;

/**
 * @author Cody
 * 
 */
public class RecieveAudio {
	ServerSocket server;
	Socket socket;
	static JTextArea text;
	ExecutorService pool;
	
	public void main(String[] args) {
		RecieveAudio ra = new RecieveAudio(Executors.newCachedThreadPool());
		ra.playAudio();
		
	}

	public RecieveAudio(ExecutorService threadPool) {
		pool = threadPool;
		JFrame frame = new JFrame();
		text = new JTextArea();

		frame.add(text);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(400, 400);
		frame.setLocation(800, 200);
		frame.setVisible(true);

		try {
			server = new ServerSocket(20);
			Log.l("Server created");
			text.append("Server created. Hosting at " + Home.getIP());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final AudioFormat format = getAudioFormat();

	public void playAudio() {
		// try{

		// Log.l("Listening for audio.");
		text.append("\nListening...");

		pool.execute(new ConnectionListenerThread(this, server));

		// }
		// catch(LineUnavailableException e) {
		// System.exit(-4);
		// }
	}// End of PlayAudio method

	public static AudioFormat getAudioFormat() {
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

	public void alertConnection(Socket s) {
		try {
			text.append("connection accepted");

			InputStream in = s.getInputStream();
			
			pool.execute(new AudioListenerThread(pool, in));
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setInfo(String s) {
		text.setText(s);
	}
}
