package proc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;

/**
 * @author Cody
 * 
 */
@SuppressWarnings("serial")
public class ContactsCellRenderer extends JLabel implements
		ListCellRenderer<User> {

	private final int SIZE = 30;

	MyIcon[] images;

	public ContactsCellRenderer() {
		setOpaque(true);
		images = new MyIcon[5];
		try {
			images[0] = new MyIcon(ImageIO.read(getClass().getResourceAsStream(
					"available.png")));
			images[1] = new MyIcon(ImageIO.read(getClass().getResourceAsStream(
					"busy.png")));
			images[2] = new MyIcon(ImageIO.read(getClass().getResourceAsStream(
					"offline.png")));
			images[3] = new MyIcon(ImageIO.read(getClass().getResourceAsStream(
					"away.png")));
			images[4] = new MyIcon(ImageIO.read(getClass().getResourceAsStream(
					"ltp.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyIcon implements Icon {
		BufferedImage image;

		public MyIcon(BufferedImage i) {
			image = i;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		@Override
		public int getIconHeight() {
			return SIZE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		@Override
		public int getIconWidth() {
			return SIZE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 * java.awt.Graphics, int, int)
		 */
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.drawImage(image, x, y, x + SIZE, y + SIZE, 0, 0,
					image.getWidth(), image.getHeight(), null);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
	 * .JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends User> list,
			User u, int index, boolean isSelected, boolean hasFocus) {
		User user = u;

		if (user == null) {
			setText("Null");
			return this;
		}
		if (!user.getGame().equals("")
				&& !user.getGame().equals("No current game")) {
			setText("<html>" + user.getName()
					+ "<br/><i><small>Currently playing " + user.getGame()
					+ "</small></i></html>");
			// Log.l("Set user text as " + user.getGame());
		} else if (user.getStatus() != null)
			setText("<html>" + user.getName() + "<br/><i><small>"
					+ user.getStatus() + "</small></i></html>");
		else
			setText(user.getName());

		if (user.getPresence() == Presence.Type.unavailable) {
			setIcon(images[2]); // Offline
			setToolTipText(user.getName() + " is offline.");
		}
		else {
			// Log.l("Mode: " + user.getMode());
			if (user.getMode() == null || user.getMode() == Mode.available) {
				setIcon(images[0]); // Online
				setToolTipText(user.getName() + " is online.");
			} else if (user.getMode() == Mode.away) {
				setIcon(images[3]); // Away
				setToolTipText(user.getName() + " is away.");
			} else if (user.getMode() == Mode.dnd) {
				setIcon(images[1]); // Busy
				setToolTipText(user.getName() + " is busy.");
			} else if (user.getMode() == Mode.xa) {
				setIcon(images[4]); // Looking to play
				setToolTipText(user.getName() + " is looking to play a game.");
			}
		}
		
		//Check for conference entry
		if (user.getName().contains("@")) {
			setIcon(images[0]); // Online
			setToolTipText(user.getName() + " group chat.");
		}

		if (index % 2 == 0)
			setBackground(Color.white);
		else
			setBackground(new Color(214, 213, 212));

		if (isSelected)
			setBackground(new Color(172, 238, 238));

		return this;
	}

}
