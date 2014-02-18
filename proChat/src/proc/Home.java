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

import org.jivesoftware.smack.XMPPException;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home implements ActionListener, KeyListener{

	JFrame frame;
	JTextArea chat;
	JTextField entry;
	XmppManager connection;
	User user;
	String serverIP;
	int port;

	public Home() throws XMPPException {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400,600);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));
		
		chat = new JTextArea("");
		entry = new JTextField("");
		
		chat.addKeyListener(this);
		entry.addKeyListener(this);
		
		user = new User("Toon", "test");
		
		chat.setEditable(false);
		chat.setBackground(new Color(245,245,245));
		
		JButton send = new JButton("Send");
		send.addActionListener(this);
		
		JPanel entryPanel = new JPanel(new GridLayout(1,2));
		
		entryPanel.add(entry);
		entryPanel.add(send,BorderLayout.EAST);
		
		masterPanel.add(chat);
		masterPanel.add(entryPanel,BorderLayout.SOUTH);
		
		frame.add(masterPanel);
		
		serverIP = "127.0.0.1";
		port = 5222;
		
		try {
		connection = new XmppManager(serverIP,port);
		connection.init();
		connection.performLogin(user.getName(), user.getPass());
		connection.setStatus(true, "Hello everyone");
		}
		catch (Exception e) {
			System.out.println("Could not connect to server");
			chat.setText("Could not connect to server " + serverIP + ":" + port);
		}
		
	}

	/**
	 * 
	 */
	public void show() {
		frame.setVisible(true);
	}
	
	private void sendMessage() {
		if (entry.getText().equals(""))
			return;
		else if (entry.getText().equals("/me")) {
			chat.setText(chat.getText() + "\n" + user.getName());
			return;
		}
		chat.setText(chat.getText() + "\n" + user.getName() + ": " + entry.getText());
		entry.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Send"))
			sendMessage();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			sendMessage();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

}
