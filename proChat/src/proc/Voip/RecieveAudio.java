package proc.Voip;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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

	public static void main(String[] args) {
		RecieveAudio ra = new RecieveAudio(Executors.newCachedThreadPool());
		ra.playAudio();

	}

	public RecieveAudio(ExecutorService threadPool) {
		pool = threadPool;
		JFrame frame = new JFrame();
		text = new JTextArea();
		
		JScrollPane scroller = new JScrollPane(text);
		scroller.setAutoscrolls(true);

		frame.add(scroller);

		frame.setSize(400, 400);
		frame.setLocation(800, 200);
		frame.setVisible(true);
	}

	final AudioFormat format = getAudioFormat();
	private SoundPlayerThread playAudioThread;

	public void playAudio() {

		text.append("\nListening @ IP " + VoiceCall.fetchExternalIP());
		
		playAudioThread = new SoundPlayerThread();
		pool.execute(playAudioThread);
		pool.execute(new UDPInThread(this));

	
	}

	public static AudioFormat getAudioFormat() {
		float sampleRate = 8000.0F;
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


	public static void setInfo(String s) {
		text.setText(s);
	}

	/**
	 * @param data
	 */
	public void recievePacket(byte[] data) {
		if (data == null)
			Log.l("Null packet recieved.");
		else
			playAudioThread.loadData(data);
	}
}
