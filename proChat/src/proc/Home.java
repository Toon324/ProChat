package proc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home implements ActionListener, KeyListener, RosterListener {

	JFrame frame;
	User user;
	String serverName, serverIP;
	JTextField to;
	// JTable contacts;
	JList<User> contacts;
	XmppManager connection;
	int port;
	ArrayList<ChatWindow> currentChats;
	String[] names = { "Name", "Online" };
	// Object[][] data = new Object[16][2];
	User[] data = { new User("Jon Carlos", ""), new User("Rob Stark", "") };
	Roster roster;

	public Home(User uu, XmppManager xmpp) throws XMPPException {

		user = uu;
		connection = xmpp;

		currentChats = new ArrayList<ChatWindow>();

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setSize(400, 608);
		frame.setTitle("ProChat");
		JPanel masterPanel = new JPanel();
		// masterPanel.setLayout(new BoxLayout(masterPanel,
		// BoxLayout.PAGE_AXIS));

		masterPanel.setLayout(new GridLayout(3, 1));

		JLabel toLabel = new JLabel("Who would you like to chat with?");
		JLabel direct = new JLabel("Directly contact this person:");

		/*
		 * DefaultTableModel defTableModel = new DefaultTableModel(data, names);
		 * contacts = new JTable(defTableModel); //contacts.setShowGrid(false);
		 * contacts.setIntercellSpacing(new Dimension(0, 0));
		 * contacts.setAutoCreateRowSorter(true);
		 */

		contacts = new JList<User>(data);
		ContactsCellRenderer cellRender = new ContactsCellRenderer();
		contacts.setCellRenderer(cellRender);
		// contacts.setCellRenderer(new DefaultListCellRenderer());

		JScrollPane scrollPane = new JScrollPane(contacts);

		// contacts.setFillsViewportHeight(true);

		to = new JTextField("");

		/*
		 * loginName.setBounds(70,30,150,20); loginPass.setBounds(70,65,150,20);
		 */

		to.addKeyListener(this);

		JButton send = new JButton("Chat");
		send.addActionListener(this);

		JPanel sendPanel = new JPanel(new GridLayout(3, 1));

		sendPanel.add(direct, BorderLayout.NORTH);
		sendPanel.add(to);
		sendPanel.add(send, BorderLayout.SOUTH);

		JButton addContact = new JButton("Add new Contact");
		addContact.setActionCommand("add");
		addContact.addActionListener(this);

		masterPanel.add(scrollPane);
		masterPanel.add(addContact);
		masterPanel.add(sendPanel, BorderLayout.SOUTH);

		frame.add(toLabel, BorderLayout.NORTH);
		frame.add(masterPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		serverIP = "129.89.185.120";
		serverName = "127.0.0.1";
		port = 5222;
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
		roster = connection.getConnection().getRoster();
		roster.addRosterListener(this);
		loadContacts();
	}

	/**
	 * 
	 */
	private void loadContacts() {
		roster.reload();
		// data = new Object[15][2];
		ensureCapacity(roster.getEntryCount());
		int x = 0;
		for (RosterEntry contact : roster.getEntries()) {
			System.out.println("Found contact: " + contact);
			String userContact = contact.getUser();
			if (userContact.indexOf("@") != -1)
				userContact = userContact
						.substring(0, userContact.indexOf("@"));
			User toAdd = new User(userContact, "");
			toAdd.setPresence(roster.getPresence(contact.getUser()));
			data[x] = toAdd;
			x++;
		}
		// Reload table
		// contacts.setModel(new DefaultTableModel(data,names));
		DefaultListModel<User> lm = new DefaultListModel<User>();

		for (int y = 0; y < data.length; y++)
			lm.add(y, data[y]);

		contacts.setModel(lm);
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
			activeChat.addToChatArea(msg.getFrom().substring(0,
					msg.getFrom().indexOf("@"))
					+ ": " + msg.getBody());

	}

	public void show() {
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Chat"))
			openChat(to.getText());
		else if (e.getActionCommand().equals("add")) {
			String toAdd = JOptionPane.showInputDialog("User to add?", "");
			if (toAdd.equals(""))
				return;

			try {
				connection.createEntry(toAdd + "@" + serverName, toAdd);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
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
		if (connectTo.equals("")) {
			/*
			 * if (contacts.getSelectedRow() == -1) return null;
			 */
			if (contacts.getSelectedIndex() == -1)
				return null;

			// connectTo = (String)
			// contacts.getValueAt(contacts.getSelectedRow(), 0);
			connectTo = ((User) contacts.getSelectedValue()).getName();
			if (connectTo == null)
				return null;
			System.out.println("Found contact to chat with: " + connectTo);
		}
		try {
			System.out.println("Creating connection to " + connectTo);
			Chat c = connection.getChatManager().createChat(
					connectTo + "@" + serverName, null);
			ChatWindow chat = new ChatWindow(user, c);
			currentChats.add(chat);

			Roster roster = connection.getConnection().getRoster();
			Presence presence = roster
					.getPresence(connectTo + "@" + serverName);
			// System.out.println("Presence of " + connectTo + ": " +
			// presence.getType());
			if (presence.getType() == Presence.Type.available) {
				chat.addToChatArea("Now chatting with " + connectTo);
			} else if (presence.getType() == Presence.Type.unavailable) {
				chat.addToChatArea(connectTo
						+ " is not Online, or does not exist.");
				chat.disableInput();
			} else {
				chat.addToChatArea("Could not find a reference to " + connectTo
						+ " on the server.");
			}

			chat.show();
			to.setText("");
			return chat;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void ensureCapacity(int i) {
		System.out.println("Data length: " + data.length + " i: " + i);
		if (data.length > i)
			return;

		// else
		// Copies data over to new array
		// Object[][] temp = new Object[data.length * 2][2];
		User[] temp = new User[i];
		/*
		 * for (int x = 0; x < data.length; x++) for (int y = 0; y <
		 * data[x].length; y++) temp[x][y] = data[x][y];
		 */
		for (int x = 0; x < data.length; x++)
			temp[x] = data[x];

		data = temp;

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jivesoftware.smack.RosterListener#entriesAdded(java.util.Collection)
	 */
	@Override
	public void entriesAdded(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jivesoftware.smack.RosterListener#entriesDeleted(java.util.Collection
	 * )
	 */
	@Override
	public void entriesDeleted(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jivesoftware.smack.RosterListener#entriesUpdated(java.util.Collection
	 * )
	 */
	@Override
	public void entriesUpdated(Collection<String> e) {
		System.out.println("Entries changed.");
		loadContacts();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jivesoftware.smack.RosterListener#presenceChanged(org.jivesoftware
	 * .smack.packet.Presence)
	 */
	@Override
	public void presenceChanged(Presence e) {
		System.out.println("Presence changed.");
		loadContacts();

	}

}
