package proc.Voip;

import java.io.InputStream;

public class AudioListenerThread extends Thread implements Runnable {

	byte[] buffer;
	InputStream is;
	int count;
	VoiceCall voice;

	public AudioListenerThread(VoiceCall call, InputStream in) {
		voice = call;
		is = in;
		buffer = new byte[call.getBufferSize()];
	}

	boolean atEnd = false;

	public void run() {

		if (!atEnd) {
			try {
				count = is.read(buffer, 0, buffer.length);
				if (count == -1) {
					// text.append("\nEnd of stream detected.");
					atEnd = true;
				}
				// text.append("\n" + buffer[0] + "  " + buffer[1] + "   " +
				// buffer[2]);
				voice.getPool().execute(new SoundPlayerThread(buffer, voice));
			} catch (Exception e) {
				e.printStackTrace();
				atEnd = true;
			}
		}

	}

}
