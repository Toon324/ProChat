package proc.Voip;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import proc.Log;

//Author: Cody

public class VoiceCall {

	ExecutorService threadPool;
	static int bufferSize = 16000;
	AudioCapture ac;
	RecieveAudio ra;

	public void main(String[] args) {
		new VoiceCall("129.89.185.223");
	}

	public VoiceCall(String ip) {

		threadPool = Executors.newCachedThreadPool();

		Log.l("Starting voice call with IP " + ip);

		ac = new AudioCapture(ip, threadPool);

		ra = new RecieveAudio(threadPool);
		ra.playAudio(); // Listen for audio

		// ac.captureAudio(); //Capture

	}

	public static String fetchExternalIP() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));

			String ip = in.readLine(); // you get the IP as a String
			return ip;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public ExecutorService getPool() {
		return threadPool;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @return the ac
	 */
	public AudioCapture getCapture() {
		return ac;
	}

	/**
	 * @return the ra
	 */
	public RecieveAudio getRecieve() {
		return ra;
	}

}
