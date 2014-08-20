package proc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * @author Cody Swendrowski
 * 
 */
public class ChatWindow implements ActionListener, KeyListener,
		HyperlinkListener {

	public JFrame frame;

	private JEditorPane chatArea;
	private JTextField chatEntry;
	private JLabel status;
	private User user;
	private Chat chat;
	private MultiUserChat groupChat;
	private HTMLEditorKit kit;

	private String fontColor = "000000";
	private String previousFontColor = fontColor;
	private String font = "Arial";

	private String lastMessageFrom = "";
	private boolean imagesEnabled = true;
	private boolean customFontsEnabled = true;

	/**
	 * @param c
	 */
	public ChatWindow(User u, Chat c) {
		chat = c;
		user = u;

		setupChatWindow("ProChat: Chatting with " + c.getParticipant());
	}

	private void setupChatWindow(String title) {
		setupFrame(title);
		setupChat();
		addScroller();
		addMenu();
	}

	private void setupFrame(String title) {
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle(title);
		frame.setLocation(550, 200);

		JButton send = new JButton("Send");
		send.addActionListener(this);

		JPanel entryPanel = new JPanel(new GridLayout(1, 2));

		status = new JLabel("");

		JPanel holder = new JPanel(new GridLayout(2, 1));
		holder.add(status, BorderLayout.NORTH);

		entryPanel.add(chatEntry);
		entryPanel.add(send, BorderLayout.EAST);

		holder.add(entryPanel);
		frame.add(holder, BorderLayout.SOUTH);

		try {
			frame.setIconImage(ImageIO.read(this.getClass()
					.getResourceAsStream("logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addScroller() {
		final JScrollPane scroller = new JScrollPane(chatArea);
		// scroller.setAutoscrolls(true);
		scroller.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {

					BoundedRangeModel brm = scroller.getVerticalScrollBar()
							.getModel();
					boolean wasAtBottom = true;

					public void adjustmentValueChanged(AdjustmentEvent e) {
						if (!brm.getValueIsAdjusting()) {
							if (wasAtBottom) {
								// System.out.println("Was at bottom!");
								brm.setValue(brm.getMaximum());
							}
						} else {
							wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm
									.getMaximum());
							// System.out.println("Bottom? " + wasAtBottom);
						}
					}
				});

		frame.add(scroller);
	}

	private void setupChat() {
		chatArea = new JEditorPane();
		chatEntry = new JTextField("");
		chatArea.setContentType("text/html");

		kit = new HTMLEditorKit();
		chatArea.setEditorKit(kit);
		chatArea.addHyperlinkListener(this);

		chatArea.addKeyListener(this);
		chatEntry.addKeyListener(this);

		chatArea.setEditable(false);
	}

	/**
	 * @param menuBar
	 */
	private void addMenu() {
		JMenuBar menuBar = new JMenuBar();
		// Build the first menu.
		JMenu menu = new JMenu("Insert");
		menuBar.add(menu);

		JMenuItem addImage = new JMenuItem("Image", KeyEvent.VK_I);
		menu.add(addImage);
		addImage.addActionListener(this);

		JMenu savedImages = new JMenu("Saved");
		menu.add(savedImages);

		JMenuItem noRead = new JMenuItem("Didn't Read");
		savedImages.add(noRead);
		noRead.addActionListener(this);

		JMenuItem troll = new JMenuItem("Troll");
		savedImages.add(troll);
		troll.addActionListener(this);

		JMenuItem desk = new JMenuItem("Desk Flip");
		savedImages.add(desk);
		desk.addActionListener(this);

		JMenuItem no = new JMenuItem("NO.");
		savedImages.add(no);
		no.addActionListener(this);

		JMenuItem lol = new JMenuItem("lol");
		savedImages.add(lol);
		lol.addActionListener(this);

		JMenuItem suprised = new JMenuItem("Suprised");
		savedImages.add(suprised);
		suprised.addActionListener(this);

		JMenuItem facepalm = new JMenuItem("Facepalm");
		savedImages.add(facepalm);
		facepalm.addActionListener(this);

		JMenuItem gusta = new JMenuItem("Me Gusta");
		savedImages.add(gusta);
		gusta.addActionListener(this);

		// HTML menu
		JMenu html = new JMenu("HTML");
		menuBar.add(html);

		JMenuItem setColor = new JMenuItem("Text Color", KeyEvent.VK_T);
		html.add(setColor);
		setColor.addActionListener(this);

		JMenuItem setFont = new JMenuItem("Font", KeyEvent.VK_F);
		html.add(setFont);
		setFont.addActionListener(this);

		try {
			BufferedImage toggleImagesIcon = ImageIO.read(getClass()
					.getResourceAsStream("imageToggle.png"));
			BufferedImage toggleTextIcon = ImageIO.read(getClass()
					.getResourceAsStream("customText.png"));

			final JButton toggleImages = new JButton(new ImageIcon(
					toggleImagesIcon));
			final JButton toggleText = new JButton(
					new ImageIcon(toggleTextIcon));

			toggleImages
					.setToolTipText("Embedded images will be displayed in chat.");
			toggleText.setToolTipText("Custom fonts and colors will be used.");

			toggleImages.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (imagesEnabled) {

						toggleImages
								.setToolTipText("Embedded images will be displayed as links.");
						toggleImages.setBackground(Color.gray);
					} else {
						toggleImages
								.setToolTipText("Embedded images will be displayed in chat.");
						toggleImages.setBackground(null);
					}
					imagesEnabled = !imagesEnabled;
				}

			});

			toggleText.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (customFontsEnabled) {

						toggleText
								.setToolTipText("Custom fonts and colors will not be used.");
						toggleText.setBackground(Color.gray);
					} else {
						toggleText
								.setToolTipText("Custom fonts and colors will be used.");
						toggleText.setBackground(null);
					}
					customFontsEnabled = !customFontsEnabled;
				}

			});

			menuBar.add(toggleImages);
			menuBar.add(toggleText);
			frame.setJMenuBar(menuBar);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * @param user2
	 * @param mu
	 */
	public ChatWindow(User u, MultiUserChat mu) {
		groupChat = mu;
		user = u;
		
		setupChatWindow("ProChat: Group chat + " + groupChat.getRoom());
	}

	public void show() {
		frame.setVisible(true);
		chatEntry.requestFocusInWindow();
		// new VoiceCall("129.89.185.120");
	}

	private void sendMessage() throws XMPPException {
		if (chatEntry.getText().equals(""))
			return;
		else if (chatEntry.getText().equals("/me")) {
			try {
				addTextToChat("<i>" + user.getName() + "</i>");

				if (chat != null)
					chat.sendMessage("/you");
				else if (groupChat != null)
					groupChat.sendMessage("/you");
				chatEntry.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		checkForGreenText(chatEntry.getText());

		if (groupChat == null) // In muc chats, user messages are fed back to
								// them, so
								// we don't need to add them ourselves.
			addToChatArea(
					"<b>" + user.getName() + "</b>: " + "<font face=\"" + font
							+ "\" color=\"" + fontColor + "\">"
							+ chatEntry.getText() + "</font>", null);

		if (chat != null)
			chat.sendMessage("<font face=\"" + font + "\" color=\"" + fontColor
					+ "\">" + chatEntry.getText() + "</font>");
		else if (groupChat != null)
			groupChat.sendMessage("<font face=\"" + font + "\" color=\""
					+ fontColor + "\">" + chatEntry.getText() + "</font>");

		chatEntry.setText("");
	}

	private void addTextToChat(String toAdd) {
		try {
			kit.insertHTML((HTMLDocument) chatArea.getDocument(), chatArea
					.getDocument().getLength(), toAdd, 0, 0, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addToChatArea(String toAdd, AttributeSet attribute) {
		// Log.l(toAdd);

		toAdd = checkSpecialCases(toAdd);

		if (toAdd.equals(""))
			return;

		try {

			String addition = "\n" + generateTimeStamp() + " " + toAdd;

			// System message
			if (!addition.contains("<b>")) {
				addTextToChat(addition);
				return;
			}
			String fromUser = addition.substring(addition.indexOf("<b>") + 3,
					addition.indexOf("</b>"));

			// Log.l("From: " + fromUser);

			if (lastMessageFrom.equals(fromUser)) {
				addition = addition.replace("<b>" + fromUser + "</b>:", "");
				addTextToChat(addition);

			} else { // New user
				addTextToChat(addition);
				lastMessageFrom = fromUser;
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		caretFix();

		if (!frame.isFocused() && user.getMode() != Mode.away
				&& user.getMode() != Mode.dnd) {
			playAlertSound();
		}
	}

	/**
	 * 
	 */
	private void playAlertSound() {
		try {
			Clip clip = AudioSystem.getClip();
			InputStream inputStream = getClass().getResourceAsStream(
					"alert.wav");
			InputStream buffedStream = new BufferedInputStream(inputStream);
			clip.open(AudioSystem.getAudioInputStream(buffedStream));
			clip.start();

			frame.toFront(); // Flash icon
		} catch (Exception e) {
			Toolkit.getDefaultToolkit().beep();
			e.printStackTrace();
		}

	}

	/**
	 * @return
	 */
	private String generateTimeStamp() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);

		String minuteText = "" + minute;

		if (hour == 0)
			hour = 12;

		if (minute < 10)
			minuteText = "0" + minute;

		return "[" + hour + ":" + minuteText + "]";
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

			sub = sub.replace("</font>", "");

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

			if (sub.contains("</font>"))
				sub = sub.replace("</font>", "");

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

		if (toAdd.matches("(.png) | (.jpg) | (.gif)")
				|| toAdd.contains("{img}")) {
			// Log.l("Enabled? " + imagesEnabled);
			if (imagesEnabled) {
				toAdd = convertImageURL(toAdd);
				return ""; // This should always be a single line message, so
							// don't check other cases.
			} else {
				if (toAdd.contains("{img}"))
					toAdd.replace("{img}", ""); // will be hyperlinked like
												// usual.

				return ""; // Still a single line message
			}

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

		if (!customFontsEnabled) {
			String temp = toAdd.substring(toAdd.indexOf("<font"),
					toAdd.indexOf("\">") + 2);
			// Log.l(temp);
			toAdd = toAdd.replace(temp, "");
			toAdd = toAdd.replace("</font>", "");
		}

		return toAdd;
	}

	/**
	 * @param toAdd
	 * @return
	 */
	private void checkForGreenText(String toAdd) {
		// System.out.println("Toad: " + toAdd);
		String temp = toAdd;
		// System.out.println("temp: " + temp);
		if (temp.contains(">>")) {
			previousFontColor = fontColor;
			fontColor = "1AFF00";
		} else
			fontColor = previousFontColor;

	}

	/**
	 * @param toAdd
	 * @return
	 */
	private String convertImageURL(String toAdd) {
		// System.out.println("Input: " + toAdd);
		if (toAdd.contains("{img}"))
			toAdd = toAdd.substring(toAdd.indexOf("{img}") + 5, toAdd.length());
		// System.out.println("URL: " + toAdd);

		try {
			BufferedImage i = ImageIO.read(new URL(toAdd));

			// System.out.println("Image size: " + i.getWidth() + ","
			// + i.getHeight());

			int x = 180 * i.getWidth() / i.getHeight();
			int y = 170 * i.getHeight() / i.getWidth();

			// System.out.println("Scaled size: " + x + "," + y);

			String imageTag = "<b>" + getFrom() + ":  </b><a href=\"" + toAdd
					+ "\"><img src=\"" + toAdd + "\" width=\"" + x
					+ "\" height=\"" + y + "\"></a>";

			addTextToChat(imageTag);
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
		else if (e.getActionCommand().equals("Text Color"))
			setColor();
		else if (e.getActionCommand().equals("Font"))
			setFont();
		else if (e.getActionCommand().equals("Didn't Read"))
			addImage("http://www.memes.at/faces/didnt_read_lol_low.gif");
		else if (e.getActionCommand().equals("Troll"))
			addImage("http://cdn.alltheragefaces.com/img/faces/png/troll-troll-face.png");
		else if (e.getActionCommand().equals("Desk Flip"))
			addImage("http://cdn.alltheragefaces.com/img/faces/png/angry-desk-flip.png");
		else if (e.getActionCommand().equals("NO."))
			addImage("http://cdn.alltheragefaces.com/img/faces/png/angry-no.png");
		else if (e.getActionCommand().equals("lol"))
			addImage("http://cdn.alltheragefaces.com/img/faces/png/laughing-lol-crazy.png");
		else if (e.getActionCommand().equals("Suprised"))
			addImage("http://cdn.alltheragefaces.com/img/faces/png/surprised-gasp.png");
		else if (e.getActionCommand().equals("Facepalm"))
			addImage("http://cdn.alltheragefaces.com/img/faces/png/annoyed-facepalm-picard.png");
		else if (e.getActionCommand().equals("Me Gusta"))
			addImage("http://cdn.alltheragefaces.com/img/faces/png/me-gusta-creepy-me-gusta.png");
	}

	/**
	 * 
	 */
	private void setFont() {
		Object[] possibilities = { "Arial", "Courier", "Times New Roman",
				"Verdana" };
		String s = (String) JOptionPane.showInputDialog(frame,
				"What font would you like to use?", "Font Choice",
				JOptionPane.PLAIN_MESSAGE, null, possibilities, font);

		System.out.println("Chosen font: " + s);
		if (s != null && !s.equals(""))
			font = s;
	}

	/**
	 * 
	 */
	private void setColor() {

		// Color result = JColorChooser.showDialog(null, "Choose a color",
		// Color.getColor(color));
		Dialog d = JColorChooser.createDialog(null, "Choose color", true,
				Home.chooser, null, null);
		d.setVisible(true);
		Color result = Home.chooser.getColor();
		System.out.println("Color returned: " + result);
		fontColor = String.format("#%02x%02x%02x", result.getRed(),
				result.getGreen(), result.getBlue());
		previousFontColor = fontColor;

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

		addImage(toAdd);

	}

	/**
	 * @param toAdd
	 */
	private void addImage(String toAdd) {
		try {
			BufferedImage i = ImageIO.read(new URL(toAdd));

			// System.out.println("Image size: " + i.getWidth() + ","
			// + i.getHeight());

			int x = 170 * i.getWidth() / i.getHeight();
			int y = 170 * i.getHeight() / i.getWidth();

			// System.out.println("Scaled size: " + x + "," + y);

			String imageTag = "<b>" + user.getName() + ":  </b><a href=\""
					+ toAdd + "\"><img src=\"" + toAdd + "\" width=\"" + x
					+ "\" height=\"" + y + "\"></a>";

			if (groupChat == null) // Prevent group chat feedback
				if (imagesEnabled)
					kit.insertHTML((HTMLDocument) chatArea.getDocument(),
							chatArea.getDocument().getLength(), imageTag, 0, 0,
							null);
				else {
					toAdd = "<b>" + user.getName() + ":  </b>"
							+ checkForHyperlink(toAdd);
					kit.insertHTML((HTMLDocument) chatArea.getDocument(),
							chatArea.getDocument().getLength(), toAdd, 0, 0,
							null);
				}

			if (chat != null)
				chat.sendMessage("{img}" + toAdd);
			else if (groupChat != null)
				groupChat.sendMessage("{img}" + toAdd);

			caretFix();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private void caretFix() {
		/*
		 * System.out.println("_________________________________");
		 * System.out.println("CaretPos: " + chatArea.getCaretPosition());
		 * System.out.println("Scroll height: " +
		 * scroller.getVerticalScrollBar().getValue());
		 * System.out.println("Doc length: " +
		 * chatArea.getDocument().getLength());
		 * 
		 * if (scroller.getVerticalScrollBar().getValue() >
		 * chatArea.getDocument() .getLength() - 50)
		 * chatArea.setCaretPosition(chatArea.getDocument().getLength());
		 */

		/*
		 * if (!brm.getValueIsAdjusting()) { if (wasAtBottom)
		 * brm.setValue(brm.getMaximum()); } else wasAtBottom = ((brm.getValue()
		 * + brm.getExtent()) == brm .getMaximum());
		 */
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
			return groupChat.getRoom().substring(0,
					groupChat.getRoom().indexOf("@"));
	}

	/**
	 * 
	 */
	public void disableInput() {
		chatEntry.setEditable(false);
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
			return groupChat.getRoom();
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
