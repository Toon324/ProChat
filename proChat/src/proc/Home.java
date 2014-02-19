package proc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jivesoftware.smack.XMPPException;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home implements ActionListener, KeyListener {

	JFrame frame;
	User user;
	String serverName, serverIP;
	JTextField to;
	ChatWindow chat;
	XmppManager connection;
	int port;

	public Home(String username, String pass) throws XMPPException {

		user = new User(username,pass);

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
			connection.setStatus(true, "Hello everyone");
		} catch (Exception e) {

		}
	}

	public void show() {
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action: " + e.getActionCommand());
		if (e.getActionCommand().equals("Chat"))
			openChat();
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			openChat();
	}

	/**
	 * 
	 */
	private void openChat() {
		try {
			chat = new ChatWindow(user.getName(), user.getPass(), to.getText(), connection, serverName);
			System.out.println("User: " + user.getName() + " Pass: " + user.getPass()
					+ " To: " + to.getText());
			chat.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
