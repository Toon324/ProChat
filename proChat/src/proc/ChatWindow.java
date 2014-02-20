package proc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * @author Cody Swendrowski
 * 
 */
public class ChatWindow implements ActionListener, KeyListener, WindowListener {

	JFrame frame;
	JTextArea chatArea;
	JTextField entry;
	JButton send;
	User user;
	String sendTo, serverName;
	Chat chat;
	XmppManager connection;

	public ChatWindow(String userName, String pass, String to, XmppManager con, String sn) throws XMPPException {
		connection = con;
		serverName = sn;
		sendTo = to;
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));

		chatArea = new JTextArea("");
		entry = new JTextField("");

		chatArea.addKeyListener(this);
		entry.addKeyListener(this);
		
		user = new User(userName, pass);

		chatArea.setEditable(false);
		chatArea.setBackground(new Color(255, 255, 255, 200));

		send = new JButton("Send");
		send.addActionListener(this);

		JPanel entryPanel = new JPanel(new GridLayout(1, 2));

		entryPanel.add(entry);
		entryPanel.add(send, BorderLayout.EAST);

		masterPanel.add(chatArea);
		masterPanel.add(entryPanel, BorderLayout.SOUTH);

		frame.add(masterPanel);
		//frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//frame.addWindowListener(this);

		try {
			
			/*
			 * connection.sendMessage("Hi", "Toon324@" + serverIP);
			 */
			chat = connection.getChatManager().createChat(
					to + "@" + serverName, null);
			
			// chat.addMessageListener(this);
			
			PacketListener myListener = new PacketListener() {

				public void processPacket(Packet packet) {
					if (packet instanceof Message) {
						Message msg = (Message) packet;
						// Process message
						recieveMessage(msg);
					}
				}

			};
			// Register the listener.
			connection.getConnection().addPacketListener(myListener, null);

			// chat.sendMessage("Hi");
			// connection.getChatManager().addChatListener(this);
			connection.printRoster();
		} catch (Exception e) {
			e.printStackTrace();
			chatArea.setText("Could not connect to server " + serverName);
		}

	}

	
	/**
	 * @param c
	 */
	public ChatWindow(User u, Chat c) {
		chat = c;
		user = u;
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));

		chatArea = new JTextArea("");
		entry = new JTextField("");

		chatArea.addKeyListener(this);
		entry.addKeyListener(this);

		chatArea.setEditable(false);
		chatArea.setBackground(new Color(255, 255, 255, 200));
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);

		JButton send = new JButton("Send");
		send.addActionListener(this);

		JPanel entryPanel = new JPanel(new GridLayout(1, 2));

		entryPanel.add(entry);
		entryPanel.add(send, BorderLayout.EAST);

		masterPanel.add(chatArea);
		masterPanel.add(entryPanel, BorderLayout.SOUTH);

		frame.add(masterPanel);
		//frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//frame.addWindowListener(this);

	}


	private void recieveMessage(Message msg) {
		if (!msg.getFrom().equals(sendTo))
			try {
				System.out.println("Creating new chat to talk to " + msg.getFrom() + ". Previous was " + sendTo);
				ChatWindow c = new ChatWindow(user.userName, user.userPass, msg.getFrom(), connection, serverName);
				c.addToChatArea("Now chatting with " + msg.getFrom());
				c.recieveMessage(msg);
				c.show();
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		else
			addToChatArea(msg.getFrom().substring(0, msg.getFrom().indexOf("@")) + ": " + msg.getBody());

	}
	
	public void show() {
		frame.setVisible(true);
		entry.requestFocusInWindow();
	}

	private void sendMessage() throws XMPPException {
		if (entry.getText().equals(""))
			return;
		else if (entry.getText().equals("/me")) {
			chatArea.setText(chatArea.getText() + "\n" + user.getName());
			chat.sendMessage(user.getName());
			return;
		}
		addToChatArea(user.getName() + ": " + entry.getText());
		chat.sendMessage(entry.getText());
		entry.setText("");
	}

	public void addToChatArea(String toAdd) {
		chatArea.setText(chatArea.getText() + "\n" + toAdd);
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

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
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
		return chat.getParticipant().substring(0, chat.getParticipant().indexOf("@"));
	}


	/**
	 * 
	 */
	public void disableInput() {
		//entry.setEditable(false);
	}

}
