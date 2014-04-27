package proc;

import java.awt.Color;
import java.awt.Component;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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

	ImageIcon[] images;

	public ContactsCellRenderer() {
		setOpaque(true);
		images = new ImageIcon[3];
		try {
			images[0] = new ImageIcon(ImageIO.read(getClass()
					.getResourceAsStream("available.png")));
			images[1] = new ImageIcon(ImageIO.read(getClass()
					.getResourceAsStream("busy.png")));
			images[2] = new ImageIcon(ImageIO.read(getClass()
					.getResourceAsStream("offline.png")));
		} catch (Exception e) {
			e.printStackTrace();
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
		setText(user.getName());

		if (user.getPresence() == Presence.Type.available) {
			if (user.getMode() == null || user.getMode() == Mode.available)
				setIcon(images[0]); //Online
			else if (user.getMode() == Mode.away || user.getMode() == Mode.dnd)
				setIcon(images[1]); //Busy or Away
		} else if (user.getPresence() == Presence.Type.unavailable)
			setIcon(images[2]); //Offline

		if (index % 2 == 0)
			setBackground(Color.white);
		else
			setBackground(new Color(214, 213, 212));

		if (isSelected)
			setBackground(new Color(172, 238, 238));

		return this;
	}

}
