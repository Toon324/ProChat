package server;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Listens for network input on a separate thread to enable faster run speeds in
 * the game. Alerts NetworkAdapter that data is available when input is found on
 * the InputStream.
 * 
 * @author Cody Swendrowski
 */
public final class ConnectionListener extends Thread {

	private NetworkAdapter adapter;
	private DataInputStream input;

	/**
	 * Creates a new ConnectionListener that listens to designated InputStream
	 * and reports to designated NetworkAdapter.
	 * 
	 * @param adapt
	 *            NetworkAdapter to alert when data is available
	 * @param in
	 *            DataInputStream to monitor
	 */
	public ConnectionListener(NetworkAdapter adapt, DataInputStream in) {
		adapter = adapt;
		input = in;
	}

	/**
	 * Alerts the NetworkAdapter if data is available.
	 */
	public void run() {
		while (!done) {
			try {
				if (input.available() > 0)
					adapter.dataAvailable();
			} catch (IOException e) {
			}
		}
		input = null;
		adapter = null;
	}
	
	boolean done = false;
	public void setDone() {
		done = true;
	}
}
