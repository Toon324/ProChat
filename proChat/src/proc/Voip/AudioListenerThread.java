package proc.Voip;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

public class AudioListenerThread extends Thread implements Runnable {

	byte[] buffer;
	InputStream is;
	int count;
	ExecutorService pool;

	public AudioListenerThread(ExecutorService p, InputStream in) {
		pool = p;
		is = in;
		buffer = new byte[VoiceCall.bufferSize];
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
				pool.execute(new SoundPlayerThread(buffer, pool));
			} catch (Exception e) {
				e.printStackTrace();
				atEnd = true;
			}
		}

	}

}
