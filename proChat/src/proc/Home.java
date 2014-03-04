package proc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home implements ActionListener, KeyListener, RosterListener {

	JFrame frame;
	JMenuBar menuBar;
	JMenu menu;
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

		// JLabel toLabel = new JLabel("Who would you like to chat with?");
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

		JPanel sendPanel = new JPanel(new GridLayout(5, 1));

		sendPanel.add(direct, BorderLayout.NORTH);
		sendPanel.add(to);
		sendPanel.add(send, BorderLayout.SOUTH);

		// Menu
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("ProChat");
		menu.getAccessibleContext().setAccessibleDescription(
				"Menu that allows signing out or exitting the program.");
		menuBar.add(menu);

		JMenuItem signOutItem = new JMenuItem("Sign Out", KeyEvent.VK_S);
		menu.add(signOutItem);
		signOutItem.addActionListener(this);

		JMenuItem exitItem = new JMenuItem("Exit Program", KeyEvent.VK_E);
		menu.add(exitItem);
		exitItem.addActionListener(this);

		// Profile menu
		JMenu profileMenu = new JMenu("Profile");
		profileMenu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(profileMenu);

		JMenuItem viewProfile = new JMenuItem("View Profile");
		profileMenu.add(viewProfile);
		viewProfile.addActionListener(this);

		JMenuItem linkSteam = new JMenuItem("Link Steam x64 ID", KeyEvent.VK_L);
		linkSteam.setActionCommand("Link");
		profileMenu.add(linkSteam);
		linkSteam.addActionListener(this);

		// Contacts menu
		JMenu contactMenu = new JMenu("Contacts");
		contactMenu.setMnemonic(KeyEvent.VK_C);
		menuBar.add(contactMenu);
		contactMenu.addActionListener(this);

		JMenuItem addContactItem = new JMenuItem("Add Contact");
		contactMenu.add(addContactItem);
		addContactItem.addActionListener(this);

		JMenuItem removeContactItem = new JMenuItem("Remove Contact");
		contactMenu.add(removeContactItem);
		removeContactItem.addActionListener(this);

		// Group menu
		JMenu groupMenu = new JMenu("Groups");
		contactMenu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(groupMenu);

		JMenuItem joinGroup = new JMenuItem("Join Group");
		groupMenu.add(joinGroup);
		joinGroup.addActionListener(this);

		JMenuItem createGroup = new JMenuItem("Create Group");
		groupMenu.add(createGroup);
		createGroup.addActionListener(this);

		frame.setJMenuBar(menuBar);
		// frame.add(masterPanel);
		frame.add(scrollPane);
		frame.add(sendPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		serverIP = "129.89.185.120";
		serverName = "127.0.0.1";
		port = 5222;
		connection.setStatus(true, "");

		PacketListener myListener = new PacketListener() {

			public void processPacket(Packet packet) {
				if (packet instanceof Message) {

					Message msg = (Message) packet;
					System.out.println("Msg: " + msg);
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
		// readSteamInfo("76561197998100303");
		readSteamInfo(user.getEmail());
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
			// System.out.println("Found contact: " + contact);
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
		String domain = msg.getFrom().substring(msg.getFrom().indexOf("@") + 1,
				msg.getFrom().indexOf("."));
		// System.out.println("Domain: " + domain);
		ChatWindow activeChat = null;

		if (domain.equals("conference")) {
			if (msg.getFrom().indexOf("/") == -1) // Room message
				return;

			from = msg.getFrom().substring(msg.getFrom().indexOf("/") + 1,
					msg.getFrom().length());
			System.out.println("Conference from: " + from);
		}

		for (ChatWindow c : currentChats) {
			System.out.println("Found Chat with " + c.getFullFrom());
			if (c.getFullFrom().equals(
					msg.getFrom().substring(0, msg.getFrom().indexOf("/")))) {
				activeChat = c;
				break;
			}
		}

		if (activeChat == null && !domain.equals("conference")) {
			ChatWindow c = openChat(from);
			c.addToChatArea("<b>" + from + "</b>: " + msg.getBody(), null);
			currentChats.add(c);
			System.out.println("Added chat: " + c.getFrom());
		} else
			activeChat.addToChatArea("<b>" + from + "</b>: " + msg.getBody(),
					null);

	}

	public void show() {
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e);
		if (e.getActionCommand().equals("Chat"))
			openChat(to.getText());
		else if (e.getActionCommand().equals("Add Contact"))
			addContact();
		else if (e.getActionCommand().equals("Create Group")) {
			MultiUserChat mu = new MultiUserChat(connection.getConnection(),
					"test@conference." + serverName);
			try {
				mu.create(user.getName());

				Form f = new Form(Form.TYPE_SUBMIT);
				FormField ff = new FormField("muc#roomconfig_persistentroom");
				ff.setType(FormField.TYPE_BOOLEAN);
				ff.addValue("0");
				ff.setRequired(true);
				ff.setLabel("Make Room Persistent?");
				System.out.println(ff.toXML()); // - output values seems good.
				f.addField(ff);

				// mu.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
				mu.sendConfigurationForm(f);

				ChatWindow cw = new ChatWindow(user, mu);
				cw.show();
				currentChats.add(cw);

			} catch (XMPPException e1) {
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("Join Group")) {
			MultiUserChat mu = new MultiUserChat(connection.getConnection(),
					"test@conference." + serverName);
			try {

				DiscussionHistory history = new DiscussionHistory();
				history.setMaxStanzas(100);

				ChatWindow cw = new ChatWindow(user, mu);
				cw.show();
				currentChats.add(cw);

				mu.join(user.userName, "", history,
						SmackConfiguration.getPacketReplyTimeout());

			} catch (XMPPException e1) {
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals("View Profile"))
			viewProfile();
		else if (e.getActionCommand().equals("Link"))
			linkID();
	}

	/**
	 * 
	 */
	private void addContact() {
		String toAdd = JOptionPane.showInputDialog("User to add?", "");
		if (toAdd.equals(""))
			return;

		try {
			connection.createEntry(toAdd + "@" + serverName, toAdd);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void linkID() {
		String toAdd = JOptionPane
				.showInputDialog(
						"What is your SteamID (64 bit)? This info can be found at http://steamidfinder.ru",
						"");
		if (toAdd == null || toAdd.equals(""))
			return;

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("username", user.getName());
		attributes.put("password", user.getPass());
		attributes.put("email", toAdd);

		Registration r = new Registration();

		r.setType(IQ.Type.SET);
		r.setTo(connection.getConnection().getServiceName());

		r.setAttributes(attributes);

		PacketFilter filter = new AndFilter(
				new PacketIDFilter(r.getPacketID()), new PacketTypeFilter(
						IQ.class));
		PacketCollector collector = connection.getConnection()
				.createPacketCollector(filter);
		connection.getConnection().sendPacket(r);

		user.setEmail(toAdd);
		readSteamInfo(toAdd);
	}

	/**
	 * 
	 */
	private void viewProfile() {
		if (user == null) {
			System.out.println("Null user!");
			return;
		}

		JFrame disp = new JFrame("Profile of " + user.getName());
		try {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

			System.out.println("Avatar: " + user.getAvatarURL());
			JLabel avatar = new JLabel(new ImageIcon(ImageIO.read(new URL(user
					.getAvatarURL()))));

			JLabel status = new JLabel(user.getSteamStatus());

			JLabel game = new JLabel("Currently playing: " + user.getGame());

			panel.add(avatar);
			panel.add(status);
			panel.add(game);

			disp.add(panel);

			disp.setSize(400, 400);
			disp.setVisible(true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

			JLabel info1 = new JLabel(
					"Could not fetch Steam profile info.");
			panel.add(info1);

			JLabel info2 = new JLabel("Are you sure that you linked the 64 bit Steam ID?");
			panel.add(info2);
			
			JLabel info3 = new JLabel("It should look similiar to this: 76561197998100405");
			panel.add(info3);
			
			JLabel info4 = new JLabel("Yours was " + user.getEmail());
			panel.add(info4);
			
			disp.add(panel);
			disp.setSize(400, 400);
			disp.setVisible(true);
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
			// System.out.println("Found contact to chat with: " + connectTo);
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
				chat.addToChatArea(
						"<i>Now chatting with " + connectTo + "</i>", null);
			} else if (presence.getType() == Presence.Type.unavailable) {
				chat.addToChatArea("<i>" + connectTo
						+ " is not Online, or does not exist.</i>", null);
				chat.disableInput();
			} else {
				chat.addToChatArea("Could not find a reference to " + connectTo
						+ " on the server.", null);
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
		// System.out.println("Data length: " + data.length + " i: " + i);
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
		String fullFrom = e.getFrom().substring(0,
				e.getFrom().indexOf("/Smack"));
		// Send message of presence change to any chatwindows with that person
		for (ChatWindow c : currentChats)
			if (c.getFullFrom().equals(fullFrom)) {
				String from = fullFrom.substring(0, fullFrom.indexOf("@"));
				if (e.isAway())
					c.addToChatArea("<i>" + from + " is now away.</i>", null);
				else if (e.isAvailable())
					c.addToChatArea("<i>" + from + " is now available.</i>",
							null);
				else
					c.addToChatArea("<i>" + from + " is now unavailable.</i>",
							null);
			}

		loadContacts();

	}

	public void readSteamInfo(String steamid) {
		System.out.println("Loading info for steamID: " + steamid);
		String turl = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=B809FE9D19152246D16A66E7ECE22ADF&steamids="
				+ steamid;
		try {
			URL surl = new URL(turl);
			URLConnection connection = surl.openConnection();
			InputStream info = connection.getInputStream();
			Scanner scan = new Scanner(info);
			while (scan.hasNext()) {
				String found = scan.next();
				/*
				 * if (found.equals("(") || found.equals(")") ||
				 * found.equals("{") || found.equals("}") || found.equals("[")
				 * || found.equals("]")) found = "";
				 */

				if (found.equals("\"avatarfull\":"))
					user.setAvatarURL(scan.next());

				else if (found.equals("\"profilestate\":"))
					user.setSteamStatus(scan.next());

				else if (found.equals("\"gameextrainfo\":"))
					user.setGame(scan.next());

				/*
				 * if (!found.equals("")) System.out.println("Read: " + found);
				 */
			}
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
