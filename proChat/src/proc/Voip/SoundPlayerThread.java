package proc.Voip;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class SoundPlayerThread implements Runnable {
	byte[] buffer;
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
	}

	public void loadData(byte[] toLoad) {
		buffer = toLoad;
		sourceDataLine.write(buffer, 0, buffer.length);
		sourceDataLine.drain();
		//Log.l("Data loaded.");
	}
}
