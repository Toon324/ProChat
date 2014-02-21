package proc;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jivesoftware.smack.packet.Presence;

/**
 * @author Cody
 *
 */
@SuppressWarnings("serial")
public class ContactsCellRenderer extends JLabel implements ListCellRenderer<User> {

	ImageIcon[] images;
	
	public ContactsCellRenderer()
	{
		setOpaque(true);
		images = new ImageIcon[3];
		images[0] = new ImageIcon( "available.png" );
		images[1] = new ImageIcon( "busy.png" );
		images[2] = new ImageIcon( "offline.png" );
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends User> list,
			User u, int index, boolean isSelected, boolean hasFocus) {
		User user = u;
		setText(user.getName());

		if (user.getPresence() == Presence.Type.available)
			setIcon(images[0]);
		else if (user.getPresence() == Presence.Type.unavailable)
			setIcon(images[2]);
		
		if (index % 2 == 0)
			setBackground(Color.white);
		else
			setBackground(Color.LIGHT_GRAY);
		
		if (isSelected)
			setBackground(new Color(172,238,238));
		
		return this;
	}

}
