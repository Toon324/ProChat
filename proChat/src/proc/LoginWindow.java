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

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPException;

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
		try {
			frame.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		if (e.getActionCommand().equals("Login"))
			login();
		else if (e.getActionCommand().equals("Register"))
			register();
		else if (e.getActionCommand().equals("Ok"))
			submitRegistration();
		else if (e.getActionCommand().equals("Cancel"))
			regframe.dispose();

	}

	JFrame regframe;
	JTextField name;
	JPasswordField pass;

	/**
	 * 
	 */
	private void register() {

		regframe = new JFrame();
		regframe.setSize(300, 500);
		regframe.setTitle("Register");
		JPanel masterPanel = new JPanel();

		masterPanel.setLayout(new GridLayout(5, 1));

		JLabel userLabel = new JLabel("Username");
		JLabel passLabel = new JLabel("Password");

		name = new JTextField("");
		pass = new JPasswordField("");

		name.addKeyListener(this);
		pass.addKeyListener(this);

		masterPanel.add(userLabel);
		masterPanel.add(name);
		masterPanel.add(passLabel);
		masterPanel.add(pass);

		JButton submit = new JButton("Ok");
		submit.addActionListener(this);

		JButton register = new JButton("Cancel");
		register.addActionListener(this);

		JPanel buttons = new JPanel(new GridLayout(0, 2));

		buttons.add(submit);
		buttons.add(register);

		regframe.add(masterPanel);
		regframe.add(buttons, BorderLayout.SOUTH);
		regframe.setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));

		regframe.setVisible(true);

	}

	private void submitRegistration() {
		AccountManager am = new AccountManager(connection.getConnection());
		try {
			String password = new String(pass.getPassword());
			System.out.println("Reg: " + name.getText() + "  " + password);
			am.createAccount(name.getText(), password);
			login();
			// System.out.println("Registered " + loginName.getText());
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
			
			if (e1.getMessage().contains("savedInfo.txt")) {
				//Still login if we can't store info
				try {
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
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "Could not login as "
							+ loginName.getText() + "\nError: " + e1.getMessage());
					e.printStackTrace();
				}
			}

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
