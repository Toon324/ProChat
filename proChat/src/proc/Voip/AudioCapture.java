package proc.Voip;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AudioCapture implements ActionListener {

	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	String IP;
	JTextArea text;
	JFrame frame;
	ExecutorService pool;

	public AudioCapture(VoiceCall vc, ExecutorService threadPool) {
		pool = threadPool;
		frame = new JFrame();
		text = new JTextArea();

		JScrollPane scroller = new JScrollPane(text);
		scroller.setAutoscrolls(true);

		JButton start = new JButton("Start");
		start.addActionListener(this);

		frame.add(scroller);
		frame.add(start, BorderLayout.SOUTH);

		frame.setSize(400, 400);
		frame.setLocation(300, 300);
		frame.setVisible(true);
		frame.setTitle("Input info");

		text.append("This IP: " + VoiceCall.fetchExternalIP());

		frame.requestFocus();
		pool.execute(new SendAudioThread(vc, text));
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
