/**
 * 
 */
package proc;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

/**
 * @author Cody
 *
 */
public class User {
	String userName, userPass;
	Type presence;
	
	public User (String name, String pass) {
		userName = name;
		userPass = pass;
		presence = Presence.Type.unavailable;
	}
	
	public String getName() {
		return userName;
	}
	
	public String getPass() {
		return userPass;
	}
	
	public Type getPresence() {
		return presence;
	}
	
	public void setPresence(Presence p) {
		presence = p.getType();
	}
}
