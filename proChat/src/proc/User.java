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
	String userName, userPass, email, avatarURL, status, game;
	Type presence;

	public User(String name, String pass) {
		userName = name;
		userPass = pass;
		email = "";
		game = "";
		avatarURL = "";
		status = "Offline";
		presence = Presence.Type.unavailable;
	}

	/**
	 * @return the game
	 */
	public String getGame() {
		return game;
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
		next = removeSteamFormatting(next);

		avatarURL = next;
	}

	public String getAvatarURL() {
		return avatarURL;
	}

	/**
	 * @param next
	 */
	public void setSteamStatus(String stat) {
		stat = removeSteamFormatting(stat);

		try {
			int id = Integer.valueOf(stat);
			switch (id) {
			case 0: status = "Offline"; return;
			case 1: status = "Online"; return;
			case 2: status = "Busy"; return;
			case 3: status = "Away"; return;
			case 4: status = "Snooze"; return;
			case 5: status = "Looking to Trade"; return;
			case 6: status = "Looking to Play"; return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getSteamStatus() {
		return status;
	}

	private String removeSteamFormatting(String input) {
		// Removes formatting
		input = input.replace("\"", "");
		input = input.replace(",", "");

		return input;
	}

	/**
	 * @param next
	 */
	public void setGame(String input) {
		input = removeSteamFormatting(input);
		
		game = input;
	}

}
