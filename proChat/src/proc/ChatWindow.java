package proc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * @author Cody Swendrowski
 * 
 */
public class ChatWindow implements ActionListener, KeyListener {

	JFrame frame;
	JTextArea chatArea;
	JTextField entry;
	XmppManager connection;
	User user;
	String serverIP, sendTo;
	int port;
	Chat chat;

	public ChatWindow(String userName, String pass, String to) throws XMPPException {
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
		chatArea.setBackground(new Color(245, 245, 245));

		JButton send = new JButton("Send");
		send.addActionListener(this);

		JPanel entryPanel = new JPanel(new GridLayout(1, 2));

		entryPanel.add(entry);
		entryPanel.add(send, BorderLayout.EAST);

		masterPanel.add(chatArea);
		masterPanel.add(entryPanel, BorderLayout.SOUTH);

		frame.add(masterPanel);

		serverIP = "129.89.185.120";
		String serverName = "127.0.0.1";
		port = 5222;

		try {
			connection = new XmppManager(serverIP, port);
			connection.init();
			connection.performLogin(user.getName(), user.getPass());
			connection.setStatus(true, "Hello everyone");
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

				private void recieveMessage(Message msg) {
					addToChatArea(msg.getFrom().substring(0, msg.getFrom().indexOf("@")) + ": " + msg.getBody());

				}
			};
			// Register the listener.
			connection.getConnection().addPacketListener(myListener, null);

			// chat.sendMessage("Hi");
			// connection.getChatManager().addChatListener(this);
			// connection.printRoster();
		} catch (Exception e) {
			e.printStackTrace();
			chatArea.setText("Could not connect to server " + serverIP + ":"
					+ port);
		}

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

	private void addToChatArea(String toAdd) {
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

}
