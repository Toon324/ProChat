package steamWrapper;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import proc.Log;

public class SteamRegister {

	private int timeout;
	private String steamID;
	private SteamListener[] listeners = new SteamListener[10];
	private int numOfListeners = 0;
	private TreeMap<String, String> infoMap = new TreeMap<String, String>();
	private ArrayList<String> watchedKeys = new ArrayList<String>();

	public enum PlayerValues {
		STEAMID("steamid:"), VISIBLITY("communityvisiblitystate:"), PROFILE_STATE(
				"profilestate:"), USERNAME("personaname:"), LAST_LOGOFF(
				"lastlogoff:"), PROFILE_URL("profileurl:"), AVATAR("avatar:"), AVATAR_MEDIUM(
				"avatarmedium:"), AVATAR_FULL("avatarfull:"), PERSONA_STATE(
				"personastate:"), PRIMARY_CLAN("primaryclanid:"), COUNTRY(
				"loccountrycode:"), STATE("locstatecode:"), CITY("loccityid:"),
				GAMEID ("gameid:"), GAME_NAME ("gameextrainfo:");

		private final String name;

		PlayerValues(String n) {
			name = n;
		}

		public String key() {
			return name;
		}
	}

	public SteamRegister(String steamid) {
		steamID = steamid;
		timeout = 5000;
		
		Executors.newCachedThreadPool().execute(new Runnable() {

			@Override
			public synchronized void run() {
				while (true) {
					try {
						this.wait(timeout);
						alert();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

		});
	}

	public String fetchValue(PlayerValues p) {
		System.out.println("Fetching " + p.key());
		return infoMap.get(p.key());
	}

	private void alert() {
		checkPlayerInfo();
	}

	/**
	 * 
	 */
	private void checkPlayerInfo() {
		String turl = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=B809FE9D19152246D16A66E7ECE22ADF&steamids="
				+ steamID;

		try {
			URL surl = new URL(turl);
			URLConnection connection = surl.openConnection();
			InputStream info = connection.getInputStream();
			Scanner scan = new Scanner(info);
			String key = "";
			String value = "";
			ArrayList<String> foundKeys = new ArrayList<String>();
			
			while (scan.hasNext()) {
				String found = scan.next();

				found = removeSteamFormatting(found);
				if (!found.equals("")) {
					if (key.equals("")) {
						key = found;
						foundKeys.add(found);
					} else {
						value = found;
						if (watchedKeys.contains(key))
							sendEvent(key, value);
						
						infoMap.put(key, value);
						key = "";
						value = "";
					}

				}

			}
			scan.close();
			
			//Game info won't show up if not their, so send out this info if it wasn't found
			if (!foundKeys.contains(PlayerValues.GAME_NAME.key()))
				if (watchedKeys.contains(PlayerValues.GAME_NAME.key()))
					sendEvent(PlayerValues.GAME_NAME.key(), "No current game");
			if (!foundKeys.contains(PlayerValues.GAMEID.key()))
				if (watchedKeys.contains(PlayerValues.GAMEID.key()))
					sendEvent(PlayerValues.GAMEID.key(), "-1");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param key
	 * @param value
	 */
	private void sendEvent(String key, String value) {
		//Produce event to send
		SteamEvent event = new SteamEvent(fetchValue(PlayerValues.USERNAME), key, value);
		
		//Send out event
		for (int x=0; x < numOfListeners; x++) {
			listeners[x].SteamUpdate(event);
		}
		
	}

	public void requestEventsFor(PlayerValues p) {
		Log.l("Watching for key " + p.key());
		watchedKeys.add(p.key());
	}

	public ArrayList<String> loadPlayerInfo() {
		String turl = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=B809FE9D19152246D16A66E7ECE22ADF&steamids="
				+ steamID;

		ArrayList<String> toReturn = new ArrayList<String>();
		try {
			URL surl = new URL(turl);
			URLConnection connection = surl.openConnection();
			InputStream info = connection.getInputStream();
			Scanner scan = new Scanner(info);
			String key = "";
			String value = "";
			while (scan.hasNext()) {
				String found = scan.next();

				found = removeSteamFormatting(found);
				if (!found.equals("")) {
					toReturn.add(found);
					if (key.equals("")) {
						key = found;
						System.out.println("Key: " + found);
					} else {
						value = found;
						System.out.println("Value: " + found);
						infoMap.put(key, value);
						key = "";
						value = "";
					}

				}

				// if (!found.equals(""))
				// Log.l("Read: " + found);

			}
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private String removeSteamFormatting(String input) {
		// Removes formatting
		input = input.replace("}", "").replace("{", "");
		input = input.replace("[", "").replace("]", "");
		input = input.replace("\"", "");
		input = input.replace(",", "");

		return input;
	}

	public void addListener(SteamListener listener) {
		ensureCapacity(numOfListeners+1);
		listeners[numOfListeners] = listener;
		numOfListeners++;
	}

	private void ensureCapacity(int amt) {
		if (amt < listeners.length)
			return;

		SteamListener[] temp = listeners;
		listeners = new SteamListener[amt + 10];

		for (int x = 0; x < temp.length; x++)
			listeners[x] = temp[x];

	}

}
