/**
 * 
 */
package proc;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

/**
 * @author Cody
 * 
 */
public class User {
	String userName, userPass, email, avatarURL, steamStatus, game, status;
	Type presence;
	Mode mode;

	public User(String name, String pass) {
		userName = name;
		userPass = pass;
		email = "";
		game = "";
		avatarURL = "";
		steamStatus = "Offline";
		presence = Presence.Type.unavailable;
		mode = null;
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

	public Mode getMode() {
		return mode;
	}

	public void setMode(Presence p) {
		mode = p.getMode();
	}

	public void setPresence(Presence p) {
		presence = p.getType();
	}

	public void setEmail(String toSet) {
		// System.out.println("Email set to: " + toSet);
		if (toSet == null)
			return;
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
			case 0:
				steamStatus = "Offline";
				return;
			case 1:
				steamStatus = "Online";
				return;
			case 2:
				steamStatus = "Busy";
				return;
			case 3:
				steamStatus = "Away";
				return;
			case 4:
				steamStatus = "Snooze";
				return;
			case 5:
				steamStatus = "Looking to Trade";
				return;
			case 6:
				steamStatus = "Looking to Play";
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSteamStatus() {
		return steamStatus;
	}

	public void refreshSteamInfo() {
		loadSteamInfo(email);
	}

	public void loadSteamInfo(String steamid) {
		// Log.l("Loading info for steamID: " + steamid);
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
					setAvatarURL(scan.next());

				else if (found.equals("\"profilestate\":"))
					setSteamStatus(scan.next());

				else if (found.equals("\"gameextrainfo\":"))
					setGame(scan.next());

				// if (!found.equals(""))
				// Log.l("Read: " + found);

			}
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	/**
	 * @param u
	 */
	public void copySteamDataFrom(User u) {
		// System.out.println("u: " + u);
		avatarURL = u.getAvatarURL();
		game = u.getGame();
		email = u.getEmail();
	}

	/**
	 * @param other
	 */
	public void setName(String other) {
		userName = other;

	}

	public String toString() {
		return "User: " + userName + " ID: " + email + " avatar: " + avatarURL
				+ " game: " + game;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(Presence p) {
		status = p.getStatus();
	}
	
	public void copyPresenceInfo(Presence p) {
		setStatus(p);
		setMode(p);
		setPresence(p);
	}

}
