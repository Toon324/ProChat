package proc;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.muc.MultiUserChat;

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
	JLabel status;
	User user;
	String sendTo, serverName;
	Chat chat;
	MultiUserChat muc;
	XmppManager connection;
	HTMLEditorKit kit;

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

		status = new JLabel("");
		
		JPanel holder = new JPanel(new GridLayout(2,1));
		holder.add(status, BorderLayout.NORTH);
		
		entryPanel.add(entry);
		entryPanel.add(send, BorderLayout.EAST);
		
		holder.add(entryPanel);

		// Menu
		JMenuBar menuBar = new JMenuBar();

		// Build the first menu.
		JMenu menu = new JMenu("Insert");
		menuBar.add(menu);

		JMenuItem addImage = new JMenuItem("Image", KeyEvent.VK_I);
		menu.add(addImage);
		addImage.addActionListener(this);

		frame.setJMenuBar(menuBar);
		frame.add(scroller);
		frame.add(holder, BorderLayout.SOUTH);
		// frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// frame.addWindowListener(this);

	}

	/**
	 * @param user2
	 * @param mu
	 */
	public ChatWindow(User u, MultiUserChat mu) {
		muc = mu;
		user = u;

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle("ProChat: Group chat " + mu.getRoom());

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

				if (chat != null)
					chat.sendMessage("/you");
				else if (muc != null)
					muc.sendMessage("/you");
				entry.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		if (muc == null)
			addToChatArea("<b>" + user.getName() + "</b>: " + entry.getText(),
					null);

		if (chat != null)
			chat.sendMessage(entry.getText());
		else if (muc != null)
			muc.sendMessage(entry.getText());

		entry.setText("");
	}

	public void addToChatArea(String toAdd, AttributeSet attribute) {

		toAdd = checkSpecialCases(toAdd);

		if (toAdd.equals(""))
			return;

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

	/**
	 * @param toAdd
	 * @return
	 */
	private String checkForHyperlink(String toAdd) {
		if (toAdd.contains("http://")) {
			String sub = toAdd.substring(toAdd.indexOf("http://"));
			if (sub.contains(" "))
				sub = sub.substring(0, sub.indexOf(" "));

			String replacement = new String("<a href" + "=\"" + sub + "\">"
					+ sub + "</a>");

			toAdd = toAdd.replace(sub, replacement);
			return toAdd;
		} else if (toAdd.contains("https://")) {
			String sub = toAdd.substring(toAdd.indexOf("https://"));
			if (sub.contains(" "))
				sub = sub.substring(0, sub.indexOf(" "));

			String replacement = new String("<a href" + "=\"" + sub + "\">"
					+ sub + "</a>");

			toAdd = toAdd.replace(sub, replacement);
			return toAdd;
		}
		return toAdd;
	}

	/**
	 * @param toAdd
	 * @return
	 */
	private String checkForSubreddit(String toAdd) {
		if (toAdd.contains("/r/")) {
			String sub = toAdd.substring(toAdd.indexOf("/r/"));
			if (sub.contains(" "))
				sub = sub.substring(0, sub.indexOf(" "));

			String reddit = new String("<a href" + "=" + "\"http://reddit.com"
					+ sub + "\">" + sub + "</a>");

			toAdd = toAdd.replace(sub, reddit);
			return toAdd;
		}
		return toAdd;
	}

	/**
	 * @param toAdd
	 * @return
	 */
	private String checkSpecialCases(String toAdd) {
		toAdd = checkForGreenText(toAdd);
		
		if (toAdd.contains("{img}")) {
			toAdd = convertImageURL(toAdd);
			return ""; // This should always be a single line message, so
						// don't check other cases.
		}
		if (toAdd.contains("/you")) {
			toAdd = toAdd.replace("/you", "<i>" + user.getName() + "</i>");
		}
		// Convert timezones
		else if (toAdd.contains("CST"))
			toAdd = convertTime(toAdd, "CST");

		else if (toAdd.contains("EST"))
			toAdd = convertTime(toAdd, "EST");

		else if (toAdd.contains("PST"))
			toAdd = convertTime(toAdd, "PST");

		else if (toAdd.contains("MST"))
			toAdd = convertTime(toAdd, "MST");

		toAdd = checkForHyperlink(toAdd);
		toAdd = checkForSubreddit(toAdd);
		
		

		return toAdd;
	}

	/**
	 * @param toAdd
	 * @return
	 */
	private String checkForGreenText(String toAdd) {
		//System.out.println("Toad: " + toAdd);
		String temp = toAdd.substring(toAdd.indexOf("</b>") + 4, toAdd.length());
		if (temp.contains("</i>"))
			temp = temp.replace("</i>", "");
		//System.out.println("temp: " + temp);
		if (temp.contains(">"))
			return toAdd.substring(0, toAdd.indexOf("</b>") + 4) + "<font color=\"1AFF00\">" + temp + "</font>";
		else
			return toAdd;
	}

	/**
	 * @param toAdd
	 * @return
	 */
	private String convertImageURL(String toAdd) {
		System.out.println("Input: " + toAdd);
		toAdd = toAdd.substring(toAdd.indexOf("{img}") + 5, toAdd.length());
		System.out.println("URL: " + toAdd);

		try {
			BufferedImage i = ImageIO.read(new URL(toAdd));

			System.out.println("Image size: " + i.getWidth() + ","
					+ i.getHeight());

			int x = 180 * i.getWidth() / i.getHeight();
			int y = 170 * i.getHeight() / i.getWidth();

			System.out.println("Scaled size: " + x + "," + y);

			String imageTag = "<b>" + getFrom() + ":  </b><a href=\"" + toAdd
					+ "\"><img src=\"" + toAdd + "\" width=\"" + x
					+ "\" height=\"" + y + "\"></a>";

			kit.insertHTML((HTMLDocument) chatArea.getDocument(), chatArea
					.getDocument().getLength(), imageTag, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toAdd;
	}

	/**
	 * @param toAdd
	 * @param string
	 */
	private String convertTime(String toAdd, String zone) {
		String original = toAdd;
		try {
			String toConvert = toAdd.substring(toAdd.indexOf(zone) - 2,
					toAdd.indexOf(zone));

			Calendar theirTime = new GregorianCalendar(
					TimeZone.getTimeZone(zone));
			theirTime.set(Calendar.HOUR, Integer.valueOf(toConvert));

			Calendar localTime = Calendar.getInstance();
			localTime.setTimeInMillis(theirTime.getTimeInMillis());

			TimeZone tz = TimeZone.getDefault();

			toAdd = toAdd.replace(toConvert, localTime.get(Calendar.HOUR) + "");
			toAdd = toAdd.replace(zone, tz.getDisplayName());

			return toAdd;
		} catch (Exception e) {
			e.printStackTrace();
			return original;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Send"))
			try {
				sendMessage();
			} catch (XMPPException e1) {
				e1.printStackTrace();
			}
		else if (e.getActionCommand().equals("Image"))
			addImage();
	}

	/**
	 * 
	 */
	private void addImage() {
		String toAdd = JOptionPane.showInputDialog(
				"What is the full URL for the image you want to add?",
				"http://");
		if (toAdd == null || toAdd.equals(""))
			return;

		try {
			BufferedImage i = ImageIO.read(new URL(toAdd));

			System.out.println("Image size: " + i.getWidth() + ","
					+ i.getHeight());

			int x = 170 * i.getWidth() / i.getHeight();
			int y = 170 * i.getHeight() / i.getWidth();

			System.out.println("Scaled size: " + x + "," + y);

			String imageTag = "<b>" + user.getName() + ":  </b><a href=\""
					+ toAdd + "\"><img src=\"" + toAdd + "\" width=\"" + x
					+ "\" height=\"" + y + "\"></a>";

			kit.insertHTML((HTMLDocument) chatArea.getDocument(), chatArea
					.getDocument().getLength(), imageTag, 0, 0, null);

			if (chat != null)
				chat.sendMessage("{img}" + toAdd);
			else if (muc != null)
				muc.sendMessage("{img}" + toAdd);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if (chat != null)
			return chat.getParticipant().substring(0,
					chat.getParticipant().indexOf("@"));
		else
			return muc.getRoom().substring(0, muc.getRoom().indexOf("@"));
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
					.showMessageDialog(null,
							"Sorry, can't launch a browser. Are you sure the url is valid?");
		}

	}

	/**
	 * 
	 */
	public void hide() {
		frame.setVisible(false);

	}

	/**
	 * @return
	 */
	public String getFullFrom() {
		if (chat != null)
			return chat.getParticipant();
		else
			return muc.getRoom();
	}

	/**
	 * @param state
	 */
	public void passState(ChatState state) {
		if (state.equals(ChatState.composing))
			status.setText(getFrom() + " is typing...");
		else
			status.setText("");
		
	}

}
