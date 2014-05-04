package steamWrapper;

public class SteamEvent {

	String name;
	String key;
	String value;

	/**
	 * @param key
	 * @param value
	 */
	public SteamEvent(String player, String k, String v) {
		name = player;
		key = k;
		value = v;
	}
	
	public String toString() {
		return name + " Key: " + key + " Value: " + value;
	}
	
	public String getUsername() {
		return name;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

}
