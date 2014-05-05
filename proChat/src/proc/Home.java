package proc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import steamWrapper.SteamEvent;
import steamWrapper.SteamListener;
import steamWrapper.SteamRegister;

/**
 * @author Cody Swendrowski
 * 
 */
public class Home implements ActionListener, MouseListener, KeyListener,
		RosterListener, SteamListener {

	static JColorChooser chooser;
	JFrame frame;
	JMenuBar menuBar;
	JMenu menu;
	User user;
	String serverName, serverIP;
	// JTable contacts;
	JList<User> contacts, activeChats;
	XmppManager connection;
	int port;
	ArrayList<ChatWindow> currentChats;
	String[] names = { "Name", "Online" };
	// Object[][] data = new Object[16][2];
	User[] data = { new User("Jon Cena", ""), new User("Rob Stark", "") };
	Roster roster;
	private String color = "#E02424";
	private static String IP;

	public Home(User uu, XmppManager xmpp) throws XMPPException {

		user = uu;
		connection = xmpp;

		currentChats = new ArrayList<ChatWindow>();
		chooser = new JColorChooser();

		/*
		 * try {
		 * UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		frame = new JFrame();
		frame.setLayout(new GridLayout(2,1));
		frame.setSize(400, 608);
		frame.setTitle("ProChat v0.1.8 ALPHA");

		contacts = new JList<User>(data);
		ContactsCellRenderer cellRender = new ContactsCellRenderer();
		contacts.setCellRenderer(cellRender);
		// contacts.setCellRenderer(new DefaultListCellRenderer());

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(contacts);

		// contacts.setFillsViewportHeight(true);

		/*
		 * loginName.setBounds(70,30,150,20); loginPass.setBounds(70,65,150,20);
		 */

		contacts.addMouseListener(this);

		// sendPanel.setBackground(Color.red);

		// UIManager.put("MenuItem.background", Color.CYAN);
		UIManager.put("MenuItem.opaque", true);

		buildMenu();

		getIP();

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
		
		scrollPane.setSize(400, 1000);
		AdPanel image = new AdPanel();
		
		frame.add(image);
//		image.setSize(image.getWidth(), 150);
		image.setMinimumSize(new Dimension(400, 150));
		
		 javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame.getContentPane());
	        frame.getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	            .addComponent(scrollPane)
	            .addComponent(image, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
	        );
	        
		try {
			frame.setIconImage(ImageIO.read(this.getClass()
					.getResourceAsStream("logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocation(300, 100);
		//frame.pack();

		serverIP = "129.89.185.120";
		serverName = "127.0.0.1";
		port = 5222;
		connection.setStatus(true, "");

		PacketListener myListener = new PacketListener() {

			public void processPacket(Packet packet) {
				if (packet instanceof Message) {

					Message msg = (Message) packet;
					// Log.l("Msg: " + msg);
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
		user.loadSteamInfo(user.getEmail());

		connection.setPresence(true, "Free to chat", Mode.available);
		
		SteamRegister sr = new SteamRegister("76561197998100303");
		sr.loadPlayerInfo();
		sr.addListener(this);
		sr.requestEventsFor(SteamRegister.PlayerValues.GAME_NAME);
	}

	/**
	 * 
	 */
	private void buildMenu() {
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

				JMenuItem setStatus = new JMenuItem("Set Status");
				profileMenu.add(setStatus);
				setStatus.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String s = JOptionPane.showInputDialog(
								"What should your status be?", user.getStatus());
						Presence p = connection.setStatus(true, s);
						user.setStatus(p);
					}

				});

				// Status submenu
				JMenu modeMenu = new JMenu("Set Mode");
				profileMenu.add(modeMenu);

				JMenuItem available = new JMenuItem("Available");
				modeMenu.add(available);
				available.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connection.setMode(true, Mode.available);
					}
				});

				JMenuItem away = new JMenuItem("Away");
				modeMenu.add(away);
				away.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connection.setMode(true, Mode.away);
					}
				});

				JMenuItem busy = new JMenuItem("Busy");
				modeMenu.add(busy);
				busy.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connection.setMode(true, Mode.dnd);
					}
				});

				JMenuItem invisible = new JMenuItem("Appear Offline");
				modeMenu.add(invisible);
				invisible.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connection.setMode(false, null);
					}
				});

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
				groupMenu.setMnemonic(KeyEvent.VK_G);
				menuBar.add(groupMenu);

				JMenuItem joinGroup = new JMenuItem("Join Group");
				groupMenu.add(joinGroup);
				joinGroup.addActionListener(this);

				// Help menu
				JMenu helpMenu = new JMenu("Help");
				helpMenu.setMnemonic(KeyEvent.VK_H);
				menuBar.add(helpMenu);

				JMenuItem showQuick = new JMenuItem("Quick Guide");
				helpMenu.add(showQuick);
				showQuick.addActionListener(this);
		
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
			// Log.l("Found contact: " + contact);
			String userContact = contact.getUser();

			if (userContact.contains("conference")) {

				data[x] = new User(userContact, "");
			} else {
				if (userContact.indexOf("@") != -1)
					userContact = userContact.substring(0,
							userContact.indexOf("@"));
				User toAdd = new User(userContact, "");

				Presence p = roster.getPresence(contact.getUser());
				toAdd.copyPresenceInfo(p);

				// if (userContact.contains("toon325"))
				// Log.l(contact.getUser() + ": " + p.getMode());
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
		ChatWindow cw = new ChatWindow(user, mu);

		currentChats.add(cw);
		try {
			mu.create(user.getName());

			Form f = new Form(Form.TYPE_SUBMIT);
			FormField ff = new FormField("muc#roomconfig_persistentroom");
			ff.setType(FormField.TYPE_BOOLEAN);
			ff.addValue("1");
			ff.setRequired(true);
			ff.setLabel("Make Room Persistent?");
			Log.l(ff.toXML()); // - output values seem good.
			f.addField(ff);

			mu.sendConfigurationForm(f);

			shouldJoin = false;
		} catch (Exception e) {
			Log.l("Room already exists.");
		}
		try {

			DiscussionHistory history = new DiscussionHistory();
			history.setSeconds(60 * 60 * 24); // Messages from the past day
			// history.setMaxStanzas(150);
			if (shouldJoin)
				mu.join(user.userName, "", history,
						SmackConfiguration.getPacketReplyTimeout());

			connection.getConnection().getRoster()
					.createEntry(name + "@conference" + serverName, name, null);

			cw.show();

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
				// Log.l("Sent id: " + message.getBody());
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
					Log.l("Sent ip: " + message.getBody());
					connection.getConnection().sendPacket(message);
					// new VoiceCall(msg.getBody());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			} else if (msg.getSubject().equals("IP")) {
				// new VoiceCall(msg.getBody());
				return;
			}
		}

		String from = msg.getFrom().substring(0, msg.getFrom().indexOf("@"));
		String domain = msg.getFrom().substring(msg.getFrom().indexOf("@") + 1,
				msg.getFrom().indexOf("."));
		// Log.l("Domain: " + domain);
		ChatWindow activeChat = null;
		
		Log.l("Message from: " + from + " at domain " + domain);

		if (domain.equals("conference")) {
			if (msg.getFrom().indexOf("/") == -1) // Room message
				return;

			from = msg.getFrom().substring(msg.getFrom().indexOf("/") + 1,
					msg.getFrom().length());
			 Log.l("Conference from: " + from);
		}

		for (ChatWindow c : currentChats) {
			// Log.l("Comparing " + from + " to " + c.getFullFrom());
			if (c.getFullFrom().equals(
					msg.getFrom().substring(0, msg.getFrom().indexOf("/")))) {
				activeChat = c;
				break;
			}
		}

		if (activeChat == null && !domain.equals("conference")) {
			Log.l("Opening new chat with " + from);
			ChatWindow c = openChat(from);
			c.addToChatArea("<b>" + from + "</b>: " + msg.getBody(), null);
			currentChats.add(c);
			Log.l("Added chat: " + c.getFrom());
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
		String cmd = e.getActionCommand();
		if (cmd.equals("Chat")) {

			String connectTo;

			if (contacts.getSelectedIndex() == -1)
				return;
			connectTo = ((User) contacts.getSelectedValue()).getName();
			if (connectTo == null)
				return;

			Log.l("ConnectTO: " + connectTo);
			if (connectTo.contains("conference")) {
				String name = connectTo.substring(0, connectTo.indexOf("@"));
				joinGroup(name);

			} else
				openChat(connectTo);
		} else if (cmd.equals("Add Contact"))
			addContact();
		else if (cmd.equals("Join Group"))
			joinGroup();
		else if (cmd.equals("View Profile"))
			viewProfile(user);
		else if (cmd.equals("Link"))
			linkID();
		else if (cmd.equals("Remove Contact"))
			removeContact();
		else if (cmd.equals("Window Color"))
			setColor();
		else if (cmd.equals("Exit Program"))
			System.exit(0);
		else if (cmd.equals("Quick Guide"))
			showQuickGuide();
		else if (cmd.equals("Sign Out")) {
			connection.getConnection().disconnect();
			new LoginWindow();
			frame.dispose();
		}
	}

	/**
	 * 
	 */
	private void showQuickGuide() {
		new QuickGuide();

	}

	private void setColor() {
		String toAdd = JOptionPane.showInputDialog(
				"What HEX color would you like your frames to be?", color);
		if (toAdd == null)
			return;

		if (!toAdd.contains("#"))
			toAdd = "#" + toAdd;
		Log.l("Color hex: " + toAdd);
		color = toAdd;
		Log.l("Color: " + Color.decode(color));
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

		// Log.l("Removing " + remove);

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
		user.loadSteamInfo(toAdd);
	}

	/**
	 * 
	 */
	private void viewProfile(User u) {
		if (u == null) {
			Log.l("Null user!");
			return;
		}

		u.refreshSteamInfo();

		JFrame disp = new JFrame("Profile of " + u.getName());
		try {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

			// Log.l("Avatar: " + user.getAvatarURL());
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
			openChat("");
	}

	/**
	 * 
	 */
	private ChatWindow openChat(String connectTo) {
		if (connectTo.equals("")) {
			//Log.l("Fetching User from List..");
			if (contacts.getSelectedIndex() == -1)
				return null;
			connectTo = ((User) contacts.getSelectedValue()).getName();
			if (connectTo == null)
				return null;
		}
		try {
			
			for (ChatWindow c : currentChats) {
				//Log.l("Comparing " + connectTo + " to " + c.getFrom());
				if (c.getFrom().equals(connectTo)) {
					c.show();
					//Log.l("Opening existing chat with " + connectTo);
					return c;
				}
			}
			
			//Log.l("Creating connection to " + connectTo);

			Chat c = connection.getChatManager().createChat(
					connectTo + "@" + serverName, null);
			ChatWindow chat = new ChatWindow(user, c);
			currentChats.add(chat);

			Roster roster = connection.getConnection().getRoster();
			Presence presence = roster
					.getPresence(connectTo + "@" + serverName);
			// Log.l("Presence of " + connectTo + ": " +
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
			//requestIP();
			chat.show();
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
		Log.l("Entries changed.");
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
		//Log.l(fullFrom + " " + e);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			openChat("");
		}

	}

	/**
	 * 
	 */
	private void requestOtherProfile() {
		String other = ((User) contacts.getSelectedValue()).getName();

		Message message = new Message();
		message.setTo(other + "@" + serverName);
		Log.l("Requesting SteamID from " + message.getTo());
		message.setSubject("ID request");
		message.setType(Message.Type.headline);
		connection.getConnection().sendPacket(message);

	}

	private void requestIP() {

		String other = ((User) contacts.getSelectedValue()).getName();

		try {
			Message message = new Message();
			message.setTo(other + "@" + serverName);
			Log.l("Requesting IP from " + message.getTo());
			message.setSubject("IP request");
			message.setBody(IP);
			message.setType(Message.Type.headline);
			Log.l("Sending IP: " + IP);
			connection.getConnection().sendPacket(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void viewOtherProfile(String id) {
		String other = ((User) contacts.getSelectedValue()).getName();

		Log.l("SteamID recieved: " + id);
		User u = new User(null, null);
		u.loadSteamInfo(id);
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
		if (IP == null)
			try {
				URL whatismyip = new URL("http://checkip.amazonaws.com");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						whatismyip.openStream()));

				String ip = in.readLine(); // you get the IP as a String
				Log.l("Found external IP of " + ip);
				IP = ip;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return IP;
	}

	/* (non-Javadoc)
	 * @see steamWrapper.SteamListener#SteamUpdate(steamWrapper.SteamEvent)
	 */
	@Override
	public void SteamUpdate(SteamEvent e) {
		Log.l("Steamupdate: " + e);
		for (int x=0; x < contacts.getModel().getSize(); x++) {
			User u = contacts.getModel().getElementAt(x);
			//Log.l("Comparing: " + u.getName() + " to " + e.getUsername() + " ? " + u.getName().equalsIgnoreCase(e.getUsername()));
			if (u.getName().equalsIgnoreCase(e.getUsername())) {
				u.setGame(e.getValue());
				Log.l("Set user " + u.getName() + " to be playing " + u.getGame());
			}
		}
		contacts.repaint();
		
	}

}
