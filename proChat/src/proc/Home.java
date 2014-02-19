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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jivesoftware.smack.XMPPException;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home implements ActionListener, KeyListener {

	JFrame frame;
	JPasswordField loginPass;
	JTextField loginName, to;
	ChatWindow chat;

	public Home() throws XMPPException {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();
		//masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));

		masterPanel.setLayout(new GridLayout(5,1));
		
		JLabel userLabel = new JLabel("Username");
		JLabel passLabel = new JLabel("Password");
		JLabel toLabel = new JLabel("Who would you like to chat with?");
		
		loginName = new JTextField("");
		loginPass = new JPasswordField();
		to = new JTextField("");
		
		/*
		loginName.setBounds(70,30,150,20);
		loginPass.setBounds(70,65,150,20);
		*/

		to.addKeyListener(this);

		JButton send = new JButton("Chat");
		send.addActionListener(this);

		JPanel sendPanel = new JPanel(new GridLayout(3, 1));

		sendPanel.add(toLabel, BorderLayout.NORTH);
		sendPanel.add(to);
		sendPanel.add(send, BorderLayout.SOUTH);

		masterPanel.add(userLabel);
		masterPanel.add(loginName);
		masterPanel.add(passLabel);
		masterPanel.add(loginPass);
		masterPanel.add(sendPanel, BorderLayout.SOUTH);

		frame.add(masterPanel);

	}

	public void show() {
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Send"))
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
			chat = new ChatWindow(loginName.getText(), new String(
					loginPass.getPassword()), to.getText());
			System.out.println("User: " + loginName.getText() + " Pass: "
					+ chat.user.getPass() + " To: " + to.getText());
			chat.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
