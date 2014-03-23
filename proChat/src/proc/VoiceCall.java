package proc;


//Author: Cody

public class VoiceCall {
	
	public VoiceCall(String ip) {
		
		Log.l("Starting voice call with IP " + ip);
		
		AudioCapture ac = new AudioCapture(ip);

		RecieveAudio ra = new RecieveAudio();
		ra.playAudio(); //Listen for audio
		
		//ac.captureAudio(); //Capture
		
	}
	

}
