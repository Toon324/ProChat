package steamWrapper;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class SteamRegister {
	
	String steamID;
	SteamListener[] listeners = new SteamListener[10];
	int numOfListeners = 0;
	TreeMap<String, String> infoMap = new TreeMap<String, String>();
	
	public SteamRegister(String steamid) {
		steamID = steamid;
	}

	public void fetchFriends() {
		
	}
	
	public ArrayList<String> fetchInfo() {
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
				/*
				 * if (found.equals("(") || found.equals(")") ||
				 * found.equals("{") || found.equals("}") || found.equals("[")
				 * || found.equals("]")) found = "";
				 */
				
				found = removeSteamFormatting(found);
				if (!found.equals("")) {
					toReturn.add(found);
					if (key.equals("")) {
						key = found;
						System.out.println("Key: " + found);
					}
					else {
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
	
	public TreeMap<String, String> getInfoMap() {
		return infoMap;
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
		numOfListeners++;
		ensureCapacity(numOfListeners);
		listeners[numOfListeners] = listener;
	}

	private void ensureCapacity(int amt) {
		if (amt < listeners.length)
			return;
		
		SteamListener[] temp = listeners;
		listeners = new SteamListener[amt + 10];
		
		for (int x=0; x < temp.length; x++)
			listeners[x] = temp[x];
		
	}

}
