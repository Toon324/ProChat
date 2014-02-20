package proc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home implements ActionListener, KeyListener {

	JFrame frame;
	User user;
	String serverName, serverIP;
	JTextField to;
	XmppManager connection;
	int port;
	ArrayList<ChatWindow> currentChats;

	public Home(String username, String pass) throws XMPPException {

		user = new User(username,pass);
		
		currentChats = new ArrayList<ChatWindow>();

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();
		// masterPanel.setLayout(new BoxLayout(masterPanel,
		// BoxLayout.PAGE_AXIS));

		masterPanel.setLayout(new GridLayout(5, 1));

		JLabel toLabel = new JLabel("Who would you like to chat with?");

		to = new JTextField("");

		/*
		 * loginName.setBounds(70,30,150,20); loginPass.setBounds(70,65,150,20);
		 */

		to.addKeyListener(this);

		JButton send = new JButton("Chat");
		send.addActionListener(this);

		JPanel sendPanel = new JPanel(new GridLayout(3, 1));

		sendPanel.add(toLabel, BorderLayout.NORTH);
		sendPanel.add(to);
		sendPanel.add(send, BorderLayout.SOUTH);

		masterPanel.add(sendPanel, BorderLayout.SOUTH);

		frame.add(masterPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		serverIP = "129.89.185.120";
		serverName = "127.0.0.1";
		port = 5222;
		
		try {
			connection = new XmppManager(serverIP, port);
			connection.init();
			connection.performLogin(user.getName(), user.getPass());
			connection.setStatus(true, "");
			
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
		} catch (Exception e) {

		}
	}
	
	private void recieveMessage(Message msg) {
		String from = msg.getFrom().substring(0, msg.getFrom().indexOf("@"));
		ChatWindow activeChat = null;
		
		System.out.println("Message from: " + from);
		
		for (ChatWindow c : currentChats) {
			System.out.println("Found Chat with " + c.getFrom());
			if (c.getFrom().equals(from)) {
				activeChat = c;
				break;
			}
		}
		
		if (activeChat == null) {
			ChatWindow c = openChat(from);
			c.addToChatArea(from + ": " + msg.getBody());
		} else
			activeChat.addToChatArea(msg.getFrom().substring(0, msg.getFrom().indexOf("@")) + ": " + msg.getBody());

	}

	public void show() {
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Chat"))
			openChat(to.getText());
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			openChat(to.getText());
	}

	/**
	 * 
	 */
	private ChatWindow openChat(String connectTo) {
		if (connectTo.equals(""))
			return null;
		try {
			System.out.println("Creating connection to " + connectTo);
			Chat c = connection.getChatManager().createChat(
				connectTo + "@" + serverName, null);
			ChatWindow chat = new ChatWindow(user, c);
			currentChats.add(chat);
			
			chat.addToChatArea("Now chatting with " + connectTo);
			
			chat.show();
			return chat;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
