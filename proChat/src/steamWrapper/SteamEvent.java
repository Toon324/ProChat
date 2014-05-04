package steamWrapper;

public class SteamEvent {

	public enum EventType {
		PLAYER_UPDATE, FRIEND_UPDATE;
	}

	EventType event;
	String key;
	String value;

	/**
	 * @param key
	 * @param value
	 */
	public SteamEvent(EventType e, String k, String v) {
		event = e;
		key = k;
		value = v;
	}
	
	public String toString() {
		return event + " Key: " + key + " Value: " + value;
	}

}
