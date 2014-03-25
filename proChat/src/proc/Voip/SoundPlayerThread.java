package proc.Voip;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class SoundPlayerThread implements Runnable{
	byte[] buffer;
	private AudioFormat format;
	private VoiceCall call;
	private boolean done = false;

	public SoundPlayerThread(byte[] buff, VoiceCall voice) {
		buffer = buff;
		format = voice.getRecieve().getAudioFormat();
		call = voice;
	}

	public void run() {
		if (done)
			return;
		try {
			/*
			 * SpeexDecoder decoder = new SpeexDecoder(); decoder.init(1, 16000,
			 * 1, false); decoder.processData(buffer, 0, buffer.length);
			 * 
			 * byte[] decoded = new byte[decoder.getProcessedDataByteSize()];
			 * 
			 * decoder.getProcessedData(decoded, 0);
			 * 
			 * text.setText(decoded[0] + "   " + decoded[1] + "   " + decoded[2]
			 * + "   " + decoded[3]);
			 * 
			 * InputStream input = new ByteArrayInputStream(decoded);
			 */
			InputStream input = new ByteArrayInputStream(buffer);

			final AudioInputStream ais = new AudioInputStream(input, format,
					buffer.length / format.getFrameSize());

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine sline = (SourceDataLine) AudioSystem.getLine(info);
			sline.open(format);
			sline.start();

			call.getRecieve().setInfo(buffer.length + "   " + buffer[0] + "   " + buffer[1]
					+ "   " + buffer[2] + "   " + buffer[3]);

			// Float audioLen = (decoded.length /
			// format.getFrameSize())
			// * format.getFrameRate();

			int bufferSize = (int) format.getSampleRate()
					* format.getFrameSize();
			byte buffer2[] = new byte[call.getBufferSize()];
			// int count2;

			ais.read(buffer2, 0, buffer2.length);
			sline.write(buffer2, 0, buffer2.length);
			sline.flush();
			sline.drain();
			// sline.stop();
			// sline.close();
			buffer2 = null;

			done = true;
		} catch (Exception e) {
			e.printStackTrace();
			done = true;
		}
	}
}
