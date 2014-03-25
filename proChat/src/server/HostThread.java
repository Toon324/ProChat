package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A seperate thread that listens for a connection without blocking the rest of
 * the game.
 * 
 * @author Cody Swendrowski
 */
public final class HostThread extends Thread {

	private DataInputStream input;
	private DataOutputStream output;
	private NetworkAdapter adapter;
	private ServerSocket socket;

	/**
	 * Creates a new HostThread.
	 * 
	 * @param net
	 *            NetworkAdapter to alert when connection is made
	 * @param socket
	 *            ServerSocket to listen on
	 */
	public HostThread(NetworkAdapter net, ServerSocket sock) {
		adapter = net;
		socket = sock;
		System.out.println("HostThread created");
	}

	@Override
	public void run() {
		try {
			if (!done) {
				Socket connection = socket.accept();
				input = new DataInputStream(connection.getInputStream());
				output = new DataOutputStream(connection.getOutputStream());
				adapter.connectionAvailable();
			} else {
				input = null;
				output = null;
				socket.close();
				if (rehost) {
					adapter.hostAvailable();
					rehost = false;
				}
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Returns the InputStream.
	 * 
	 * @return input
	 */
	public DataInputStream getInputStream() {
		return input;
	}

	/**
	 * Returns the OutputStream.
	 * 
	 * @return output
	 */
	public DataOutputStream getOutputStream() {
		return output;
	}

	boolean done = false;

	/**
	 * 
	 */
	public void setDone() {
		done = true;

	}

	boolean rehost = false;

	/**
	 * 
	 */
	public void setShouldRehost() {
		rehost = true;
	}

}
