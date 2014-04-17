package proc.Voip;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import proc.Log;

public class SoundPlayerThread implements Runnable {
	byte[] buffer;
	private boolean hasData = false;
	SourceDataLine sourceDataLine;

	public SoundPlayerThread() {
		try {
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, RecieveAudio.getAudioFormat());
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(RecieveAudio.getAudioFormat());
			sourceDataLine.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
//		while (true) {
//			//Log.l("HasData: " + hasData);
//			if (hasData) {
//				Log.l("HasData.");
//				try {
//					sourceDataLine.write(buffer, 0, buffer.length);
//					sourceDataLine.drain();
//					hasData = false;
//					Log.l("Data played to sound output.");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

	public void loadData(byte[] toLoad) {
		buffer = toLoad;
		sourceDataLine.write(buffer, 0, buffer.length);
		sourceDataLine.drain();
		hasData = true;
		//Log.l("Data loaded.");
	}
}
