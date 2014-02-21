package proc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

/**
 * @author Cody Swendrowski
 * 
 */
public class ChatWindow implements ActionListener, KeyListener {

	JFrame frame;
	JTextArea chatArea;
	JTextField entry;
	JButton send;
	User user;
	String sendTo, serverName;
	Chat chat;
	XmppManager connection;

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

		chatArea = new JTextArea("");
		entry = new JTextField("");

		chatArea.addKeyListener(this);
		entry.addKeyListener(this);

		chatArea.setEditable(false);
		//chatArea.setBackground(new Color(255, 255, 255, 200));
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		
		JScrollPane scroller = new JScrollPane(chatArea);
		scroller.setAutoscrolls(true);

		JButton send = new JButton("Send");
		send.addActionListener(this);

		JPanel entryPanel = new JPanel(new GridLayout(1, 2));

		entryPanel.add(entry);
		entryPanel.add(send, BorderLayout.EAST);

		frame.add(scroller);
		frame.add(entryPanel, BorderLayout.SOUTH);
		//frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//frame.addWindowListener(this);

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
