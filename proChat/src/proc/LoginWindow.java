package proc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.provider.VCardProvider;

/**
 * @author Cody
 * 
 */
public class LoginWindow implements ActionListener, KeyListener {
	JFrame frame;
	File saved;
	XmppManager connection;

	public LoginWindow() {
		frame = new JFrame();

		String user = "";
		String pass = "";

		saved = new File("savedInfo.txt");
		if (!saved.exists())
			try {
				saved.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else {
			try {
				// Load in the saved login info
				Scanner reader = new Scanner(saved);
				reader.useDelimiter("\t");
				while (reader.hasNext()) {
					String found = reader.next();
					// System.out.println("Found: " + found);
					if (user.equals(""))
						user = found;
					else if (pass.equals(""))
						pass = found;
				}
				reader.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		frame.addKeyListener(this);

		String serverIP = "129.89.185.120";
		int port = 5222;

		try {
			connection = new XmppManager(serverIP, port);
			connection.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		DisplayInputWindow(user, pass);
	}

	JTextField loginName;
	JPasswordField loginPass;

	private void DisplayInputWindow(String user, String pass) {
		frame = new JFrame();
		frame.setSize(400, 600);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();

		masterPanel.setLayout(new GridLayout(5, 1));

		JLabel userLabel = new JLabel("Username");
		JLabel passLabel = new JLabel("Password");

		loginName = new JTextField(user);
		loginPass = new JPasswordField(pass);

		loginName.addKeyListener(this);
		loginPass.addKeyListener(this);

		masterPanel.add(userLabel);
		masterPanel.add(loginName);
		masterPanel.add(passLabel);
		masterPanel.add(loginPass);

		JButton submit = new JButton("Login");
		submit.addActionListener(this);

		JButton register = new JButton("Register");
		register.addActionListener(this);

		JPanel buttons = new JPanel(new GridLayout(0, 2));

		buttons.add(submit);
		buttons.add(register);

		frame.add(masterPanel);
		frame.add(buttons, BorderLayout.SOUTH);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));

		frame.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Login")) {
			login();
		} else if (e.getActionCommand().equals("Register"))
			register();

	}

	/**
	 * 
	 */
	private void register() {
		int i = JOptionPane.showConfirmDialog(frame,
				"Are you sure you want to register as " + loginName.getText()
						+ " with the given password?");

		if (i == JOptionPane.NO_OPTION)
			return;

		AccountManager am = new AccountManager(connection.getConnection());
		try {
			String pass = new String(loginPass.getPassword());
			System.out.println("Reg: " + loginName.getText() + "  " + pass);
			am.createAccount(loginName.getText(),
					pass);
			//System.out.println("Registered " + loginName.getText());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			login();

	}

	/**
	 * 
	 */
	private void login() {
		// Write info to .txt
		try {
			PrintWriter writer = new PrintWriter(saved);
			writer.write(loginName.getText() + "\t"
					+ new String(loginPass.getPassword()));
			writer.close();

			User user = new User(loginName.getText(), new String(
					loginPass.getPassword()));

			connection.getConnection().login(user.getName(), user.getPass());

			user.setEmail(connection.getConnection().getAccountManager()
					.getAccountAttribute("email"));

			Home home = new Home(user, connection);
			home.show();
			JOptionPane.showMessageDialog(frame, "Successfully logged in as "
					+ loginName.getText());
			frame.dispose();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, "Could not login as "
					+ loginName.getText() + "\nError: " + e1.getMessage());

			e1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
