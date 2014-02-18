package proc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home {

	JFrame frame;

	public Home() {
		frame = new JFrame();
		frame.setSize(400,600);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));
		
		JTextField chat = new JTextField("");
		JTextField entry = new JTextField("");
		
		chat.setEditable(false);
		//chat.setBackground(Color.LIGHT_GRAY);
		
		JButton send = new JButton("Send");
		
		JPanel entryPanel = new JPanel(new GridLayout(1,0));
		
		entryPanel.add(entry);
		entryPanel.add(send,BorderLayout.EAST);
		
		masterPanel.add(chat);
		masterPanel.add(entryPanel,BorderLayout.SOUTH);
		
		frame.add(masterPanel);
	}

	/**
	 * 
	 */
	public void show() {
		frame.setVisible(true);
	}

}
