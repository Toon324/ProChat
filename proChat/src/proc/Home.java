package proc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
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
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.jivesoftware.smack.Chat;
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
public class Home implements ActionListener, MouseListener, KeyListener,
		RosterListener {

	JFrame frame;
	JMenuBar menuBar;
	JMenu menu;
	User user;
	String serverName, serverIP;
	JTextField to;
	// JTable contacts;
	JList<User> contacts, activeChats;
	XmppManager connection;
	int port;
	ArrayList<ChatWindow> currentChats;
	String[] names = { "Name", "Online" };
	// Object[][] data = new Object[16][2];
	User[] data = { new User("Jon Carlos", ""), new User("Rob Stark", "") };
	Roster roster;
	private String color = "#E02424";
	private static String IP;

	public Home(User uu, XmppManager xmpp) throws XMPPException {

		user = uu;
		connection = xmpp;

		currentChats = new ArrayList<ChatWindow>();

		/*
		 * try {
		 * UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		frame = new JFrame();
		frame.setSize(400, 608);
		frame.setTitle("ProChat");

		JLabel direct = new JLabel("Directly contact this person:");

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

		contacts.addMouseListener(this);

		JButton send = new JButton("Chat");
		send.addActionListener(this);

		JPanel sendPanel = new JPanel(new GridLayout(5, 1));
		// sendPanel.setBackground(Color.red);

		// UIManager.put("MenuItem.background", Color.CYAN);
		UIManager.put("MenuItem.opaque", true);

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

		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));

			String ip = in.readLine(); // you get the IP as a String
			System.out.println("Found external IP of " + ip);
			IP = ip;
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * //Options menu JMenu options = new JMenu("Options");
		 * options.setMnemonic(KeyEvent.VK_O); menuBar.add(options);
		 * 
		 * JMenuItem frameColor = new JMenuItem("Window Color");
		 * options.add(frameColor); frameColor.addActionListener(this);
		 */

		frame.setJMenuBar(menuBar);
		// frame.add(masterPanel);
		frame.add(scrollPane);
		frame.add(sendPanel, BorderLayout.SOUTH);
		try {
			frame.setIconImage(ImageIO.read(this.getClass()
					.getResourceAsStream("logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		serverIP = "129.89.185.120";
		serverName = "127.0.0.1";
		port = 5222;
		connection.setStatus(true, "");

		PacketListener myListener = new PacketListener() {

			public void processPacket(Packet packet) {
				if (packet instanceof Message) {

					Message msg = (Message) packet;
					// System.out.println("Msg: " + msg);
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
		User u = readSteamInfo(user.getEmail());
		user.copySteamDataFrom(u);
	}

	/**
	 * 
	 */
	private void loadContacts() {
		roster.reload();
		data = new User[roster.getEntryCount()];
		// ensureCapacity(roster.getEntryCount());
		int x = 0;
		for (RosterEntry contact : roster.getEntries()) {
			// System.out.println("Found contact: " + contact);
			String userContact = contact.getUser();

			if (userContact.contains("conference")) {

				data[x] = new User(userContact, "");
			} else {
				if (userContact.indexOf("@") != -1)
					userContact = userContact.substring(0,
							userContact.indexOf("@"));
				User toAdd = new User(userContact, "");
				toAdd.setPresence(roster.getPresence(contact.getUser()));
				data[x] = toAdd;
			}
			x++;
		}
		// Reload table
		// contacts.setModel(new DefaultTableModel(data,names));
		DefaultListModel<User> lm = new DefaultListModel<User>();

		for (int y = 0; y < data.length; y++)
			lm.add(y, data[y]);

		contacts.setModel(lm);
	}

	/**
	 * @param name
	 */
	private void joinGroup(String name) {
		MultiUserChat mu = new MultiUserChat(connection.getConnection(), name
				+ "@conference." + serverName);
		boolean shouldJoin = true;
		try {
			mu.create(user.getName());

			Form f = new Form(Form.TYPE_SUBMIT);
			FormField ff = new FormField("muc#roomconfig_persistentroom");
			ff.setType(FormField.TYPE_BOOLEAN);
			ff.addValue("1");
			ff.setRequired(true);
			ff.setLabel("Make Room Persistent?");
			System.out.println(ff.toXML()); // - output values seem good.
			f.addField(ff);

			mu.sendConfigurationForm(f);

			shouldJoin = false;
		} catch (Exception e) {
			System.out.println("Room already exists.");
		}
		try {

			DiscussionHistory history = new DiscussionHistory();
			history.setMaxStanzas(150);

			ChatWindow cw = new ChatWindow(user, mu);
			cw.show();
			currentChats.add(cw);

			if (shouldJoin)
				mu.join(user.userName, "", history,
						SmackConfiguration.getPacketReplyTimeout());

			connection.getConnection().getRoster()
					.createEntry(name + "@conference" + serverName, name, null);

		} catch (XMPPException e1) {
			e1.printStackTrace();
		}

	}

	private void recieveMessage(Message msg) {

		if (msg.getSubject() != null) {
			if (msg.getSubject().equals("ID request")) {
				Message message = new Message();
				message.setTo(msg.getFrom());
				message.setSubject("ID");
				message.setBody(user.getEmail());
				message.setType(Message.Type.headline);
				// System.out.println("Sent id: " + message.getBody());
				connection.getConnection().sendPacket(message);
				return;
			} else if (msg.getSubject().equals("ID")) {
				viewOtherProfile(msg.getBody());
				return;
			} else if (msg.getSubject().equals("IP request")) {
				try {
					Message message = new Message();
					message.setTo(msg.getFrom());
					message.setSubject("IP");
					message.setBody(IP);
					message.setType(Message.Type.headline);
					System.out.println("Sent ip: " + message.getBody());
					connection.getConnection().sendPacket(message);
					// new VoiceCall(msg.getBody());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			} else if (msg.getSubject().equals("IP")) {
				//new VoiceCall(msg.getBody());
				return;
			}
		}

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
			// System.out.println("Conference from: " + from);
		}

		for (ChatWindow c : currentChats) {
			// System.out.println("Found Chat with " + c.getFullFrom());
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
		} else {
			activeChat.addToChatArea("<b>" + from + "</b>: " + msg.getBody(),
					null);
			if (!activeChat.frame.isVisible())
				activeChat.frame.setVisible(true);
		}

	}

	public void show() {
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Chat")) {

			String connectTo = to.getText();

			if (connectTo.equals("")) {
				if (contacts.getSelectedIndex() == -1)
					return;
				connectTo = ((User) contacts.getSelectedValue()).getName();
				if (connectTo == null)
					return;
			}
			System.out.println("ConnectTO: " + connectTo);
			if (connectTo.contains("conference")) {
				String name = connectTo.substring(0, connectTo.indexOf("@"));
				joinGroup(name);

			} else
				openChat(connectTo);
		} else if (e.getActionCommand().equals("Add Contact"))
			addContact();
		else if (e.getActionCommand().equals("Join Group"))
			joinGroup();
		else if (e.getActionCommand().equals("View Profile"))
			viewProfile(user);
		else if (e.getActionCommand().equals("Link"))
			linkID();
		else if (e.getActionCommand().equals("Remove Contact"))
			removeContact();
		else if (e.getActionCommand().equals("Window Color"))
			setColor();
		else if (e.getActionCommand().equals("Exit Program"))
			System.exit(0);
		else if (e.getActionCommand().equals("Sign Out")) {
			connection.getConnection().disconnect();
			new LoginWindow();
			frame.dispose();
		}
	}

	private void setColor() {
		String toAdd = JOptionPane.showInputDialog(
				"What HEX color would you like your frames to be?", color);
		if (toAdd == null)
			return;

		if (!toAdd.contains("#"))
			toAdd = "#" + toAdd;
		System.out.println("Color hex: " + toAdd);
		color = toAdd;
		System.out.println("Color: " + Color.decode(color));
		frame.getContentPane().setBackground(Color.decode(color));
		frame.setForeground(Color.decode(color));
	}

	/**
	 * 
	 */
	private void removeContact() {
		if (contacts.getSelectedIndex() == -1)
			return;

		String remove = ((User) contacts.getSelectedValue()).getName();
		if (remove == null)
			return;

		int i = JOptionPane.showConfirmDialog(frame,
				"Are you sure you want to remove " + remove + " as a contact?");

		if (i == JOptionPane.NO_OPTION)
			return;

		// System.out.println("Removing " + remove);

		try {
			connection.getConnection().getRoster()
					.removeEntry(roster.getEntry(remove + "@" + serverName));
			loadContacts();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void joinGroup() {
		String toAdd = JOptionPane
				.showInputDialog(
						"What group would you like to join? If it doesn't already exist, it will be created.",
						"");

		if (toAdd == null || toAdd.equals(""))
			return;

		joinGroup(toAdd);
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
		connection.getConnection().createPacketCollector(filter);
		connection.getConnection().sendPacket(r);

		user.setEmail(toAdd);
		readSteamInfo(toAdd);
	}

	/**
	 * 
	 */
	private void viewProfile(User u) {
		if (u == null) {
			System.out.println("Null user!");
			return;
		}

		JFrame disp = new JFrame("Profile of " + u.getName());
		try {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

			// System.out.println("Avatar: " + user.getAvatarURL());
			JLabel avatar = new JLabel(new ImageIcon(ImageIO.read(new URL(u
					.getAvatarURL()))));

			JLabel status = new JLabel(user.getSteamStatus());

			JLabel game = new JLabel("Currently playing: " + u.getGame());

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

			JLabel info1 = new JLabel("Could not fetch Steam profile info.");
			panel.add(info1);

			JLabel info2 = new JLabel(
					"Are you sure that you linked the 64 bit Steam ID?");
			panel.add(info2);

			JLabel info3 = new JLabel(
					"It should look similiar to this: 76561197998100405");
			panel.add(info3);

			JLabel info4 = new JLabel("Yours was " + u.getEmail());
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

			if (contacts.getSelectedIndex() == -1)
				return null;
			connectTo = ((User) contacts.getSelectedValue()).getName();
			if (connectTo == null)
				return null;
		}
		try {
			// System.out.println("Creating connection to " + connectTo);

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
			requestIP();
			chat.show();
			to.setText("");
			return chat;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

	private User readSteamInfo(String steamid) {
		User u = new User("", "");
		u.setEmail(steamid);
		// System.out.println("Loading info for steamID: " + steamid);
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
					u.setAvatarURL(scan.next());

				else if (found.equals("\"profilestate\":"))
					u.setSteamStatus(scan.next());

				else if (found.equals("\"gameextrainfo\":"))
					u.setGame(scan.next());

				// if (!found.equals(""))
				// System.out.println("Read: " + found);

			}
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			requestOtherProfile();
		}

	}

	/**
	 * 
	 */
	private void requestOtherProfile() {
		String other = ((User) contacts.getSelectedValue()).getName();

		Message message = new Message();
		message.setTo(other + "@" + serverName);
		System.out.println("Requesting id from " + message.getTo());
		message.setSubject("ID request");
		message.setType(Message.Type.headline);
		connection.getConnection().sendPacket(message);

	}

	private void requestIP() {
		String other = ((User) contacts.getSelectedValue()).getName();

		try {
			Message message = new Message();
			message.setTo(other + "@" + serverName);
			System.out.println("Requesting IP from " + message.getTo());
			message.setSubject("IP request");
			message.setBody(Inet4Address.getLocalHost().getHostAddress());
			message.setType(Message.Type.headline);
			connection.getConnection().sendPacket(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void viewOtherProfile(String id) {
		String other = ((User) contacts.getSelectedValue()).getName();

		System.out.println("id recieved: " + id);
		User u = readSteamInfo(id);
		u.setName(other);

		viewProfile(u);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public static String getIP() {
		return IP;
	}

}
