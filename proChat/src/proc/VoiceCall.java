package proc;


//Author: Cody

public class VoiceCall {
	
	public VoiceCall() {
		
		AudioCapture ac = new AudioCapture();
		
		
		RecieveAudio ra = new RecieveAudio();
		ra.playAudio(); //Listen for audio
		
	}
	

}
