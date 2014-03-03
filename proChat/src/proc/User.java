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
	String userName, userPass, email, avatarURL;
	Type presence;
	
	public User (String name, String pass) {
		userName = name;
		userPass = pass;
		email="";
		avatarURL = "";
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
	
	public void setEmail(String toSet) {
		System.out.println("Email set to: " + toSet);
		email = toSet;
	}

	/**
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param next
	 */
	public void setAvatarURL(String next) {
		//Removes formatting
		next = next.replace("\"", "");
		next = next.replace(",", "");
		
		avatarURL = next;
	}
	
	public String getAvatarURL() {
		return avatarURL;
	}
	
}
