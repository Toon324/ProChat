package proc;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

/**
 * @author Cody Swendrowski
 * 
 */
public class ChatWindow implements ActionListener, KeyListener,
		HyperlinkListener {

	JFrame frame;
	JEditorPane chatArea;
	JTextField entry;
	JButton send;
	User user;
	String sendTo, serverName;
	Chat chat;
	XmppManager connection;
	HTMLEditorKit kit;

	private static final String ERROR = "ERROR";
	private static final String MESSAGE = "msg";

	/**
	 * @param c
	 */
	public ChatWindow(User u, Chat c) {
		chat = c;
		user = u;

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle("ProChat: Chatting with " + c.getParticipant());

		chatArea = new JEditorPane();
		entry = new JTextField("");
		chatArea.setContentType("text/html");

		kit = new HTMLEditorKit();
		chatArea.setEditorKit(kit);
		chatArea.addHyperlinkListener(this);

		/*
		 * StyleSheet styleSheet = kit.getStyleSheet(); styleSheet.addRule("." +
		 * MESSAGE + " {font: 10px monaco; color: black; }"); styleSheet
		 * .addRule("." + ERROR +
		 * " {font: 10px monaco; color: #ff2222; background-color : #cccccc; }"
		 * );
		 * 
		 * Document doc = kit.createDefaultDocument();
		 * chatArea.setDocument(doc);
		 */

		chatArea.addKeyListener(this);
		entry.addKeyListener(this);

		chatArea.setEditable(false);

		// chatArea.setBackground(new Color(255, 255, 255, 200));

		JScrollPane scroller = new JScrollPane(chatArea);
		scroller.setAutoscrolls(true);

		JButton send = new JButton("Send");
		send.addActionListener(this);

		JPanel entryPanel = new JPanel(new GridLayout(1, 2));

		entryPanel.add(entry);
		entryPanel.add(send, BorderLayout.EAST);

		frame.add(scroller);
		frame.add(entryPanel, BorderLayout.SOUTH);
		// frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// frame.addWindowListener(this);

	}

	public void show() {
		frame.setVisible(true);
		entry.requestFocusInWindow();
	}

	private void sendMessage() throws XMPPException {
		if (entry.getText().equals(""))
			return;
		else if (entry.getText().equals("/me")) {
			try {
				kit.insertHTML((HTMLDocument) chatArea.getDocument(), chatArea
						.getDocument().getLength(), "<i>" + user.getName()
						+ "</i>", 0, 0, null);
				chat.sendMessage("/you");
				entry.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		addToChatArea("<b>" + user.getName() + "</b>: " + entry.getText(), null);
		chat.sendMessage(entry.getText());
		entry.setText("");
	}

	public void addToChatArea(String toAdd, AttributeSet attribute) {

		if (toAdd.equals("/you")) {
			try {
				kit.insertHTML((HTMLDocument) chatArea.getDocument(), chatArea
						.getDocument().getLength(), "<i>" + user.getName()
						+ "</i>", 0, 0, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);

		String minuteText = "" + minute;

		if (minute < 10)
			minuteText = "0" + minute;

		try {
			/*
			 * chatArea.getDocument().insertString(
			 * chatArea.getDocument().getLength(), "\n[" + hour + ":" +
			 * minuteText + "] " + toAdd, attribute);
			 */

			String addition = "\n[" + hour + ":" + minuteText + "] " + toAdd;

			/*
			 * AffineTransform affinetransform = new AffineTransform();
			 * FontRenderContext frc = new
			 * FontRenderContext(affinetransform,true,true); Font font = new
			 * Font("Tahoma", Font.PLAIN, 12); int textWidth =
			 * (int)(font.getStringBounds(addition, frc).getWidth());
			 * 
			 * ParagraphView pv = new ParagraphView((Element) chatArea);
			 */

			kit.insertHTML((HTMLDocument) chatArea.getDocument(), chatArea
					.getDocument().getLength(), addition, 0, 0, null);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		chatArea.setCaretPosition(chatArea.getDocument().getLength());

		if (!frame.isFocused()) {
			try {
				Clip clip = AudioSystem.getClip();
				InputStream inputStream = getClass().getResourceAsStream(
						"alert.wav");
				InputStream buffedStream = new BufferedInputStream(inputStream);
				clip.open(AudioSystem.getAudioInputStream(buffedStream));
				clip.start();
			} catch (Exception e) {
				Toolkit.getDefaultToolkit().beep();
				e.printStackTrace();
			}
		}

		frame.toFront();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Send"))
			try {
				sendMessage();
			} catch (XMPPException e1) {
				e1.printStackTrace();
			}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			try {
				sendMessage();
			} catch (XMPPException e1) {
				e1.printStackTrace();
			}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * @return
	 */
	public Chat getChat() {
		return chat;
	}

	/**
	 * @return
	 */
	public String getFrom() {
		return chat.getParticipant().substring(0,
				chat.getParticipant().indexOf("@"));
	}

	/**
	 * 
	 */
	public void disableInput() {
		// entry.setEditable(false);
	}

	/**
	 * 
	 */
	public void clear() {
		chatArea.setText("");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event
	 * .HyperlinkEvent)
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
			return;
		try {
			System.out.println("URL: " + e.getURL());
			URI myURI = new URI(e.getURL().toString());
			Desktop.getDesktop().browse(myURI);

		} catch (Throwable e1) {
			e1.printStackTrace();
			JOptionPane
					.showMessageDialog(null, "Sorry, can't launch a browser. Are you sure the url is valid?");
		}

	}

}
