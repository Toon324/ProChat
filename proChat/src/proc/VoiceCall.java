package proc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//Author: Cody

public class VoiceCall {
	
	ExecutorService threadPool;
	int bufferSize = 16000;
	
	public VoiceCall(String ip) {
		
		threadPool = Executors.newCachedThreadPool();
		
		Log.l("Starting voice call with IP " + ip);
		
		AudioCapture ac = new AudioCapture(ip, this);

		RecieveAudio ra = new RecieveAudio(this);
		ra.playAudio(); //Listen for audio
		
		//ac.captureAudio(); //Capture
		
	}
	
	public ExecutorService getPool() {
		return threadPool;
	}
	

}
