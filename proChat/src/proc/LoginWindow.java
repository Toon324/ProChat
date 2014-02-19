package proc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jivesoftware.smack.XMPPException;

/**
 * @author Cody
 * 
 */
public class LoginWindow implements ActionListener {
	JFrame frame;
	File saved;

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
				Scanner reader = new Scanner(saved);
				reader.useDelimiter("\t");
				while (reader.hasNext()) {
					String found = reader.next();
					System.out.println("Found: " + found);
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

		DisplayInputWindow(user,pass);
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

		masterPanel.add(userLabel);
		masterPanel.add(loginName);
		masterPanel.add(passLabel);
		masterPanel.add(loginPass);

		JButton submit = new JButton("Login");
		submit.addActionListener(this);

		masterPanel.add(submit, BorderLayout.SOUTH);

		frame.add(masterPanel);
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
			// Write info to .txt
			try {
				PrintWriter writer = new PrintWriter(saved);
				writer.write(loginName.getText() + "\t"
						+ new String(loginPass.getPassword()));
				writer.close();

				Home home = new Home(loginName.getText(), new String(
						loginPass.getPassword()));
				home.show();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			frame.dispose();
		}

	}
}
