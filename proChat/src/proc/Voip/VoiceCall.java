package proc.Voip;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import proc.Log;


//Author: Cody

public class VoiceCall {
	
	ExecutorService threadPool;
	int bufferSize = 16000;
	AudioCapture ac;
	RecieveAudio ra;
	
	public void main(String[] args) {
		new VoiceCall("129.89.185.223");
	}
	
	public VoiceCall(String ip) {
		
		threadPool = Executors.newCachedThreadPool();
		
		Log.l("Starting voice call with IP " + ip);
		
		ac = new AudioCapture(ip, this);

		ra = new RecieveAudio(this);
		ra.playAudio(); //Listen for audio
		
		//ac.captureAudio(); //Capture
		
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
