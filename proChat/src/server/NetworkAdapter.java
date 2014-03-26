package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Provides convenience method for connecting to a specified IP address and
 * port, or hosting on the IP on a given port.
 * 
 * Provides access to input and output stream for receiving and sending data
 * over the network.
 * 
 * A ConnectionListener is created to determine if any data is available on the
 * input stream. This is to prevent a system resource lag in attempting to
 * constantly read from the stream.
 * 
 * @author Cody Swendrowski
 */
public class NetworkAdapter {

	protected boolean dataAvailable;
	protected DataInputStream input;
	protected DataOutputStream output;
	protected HostThread hostThread;
	protected int port;
	protected boolean connected;
	protected Socket connection;
	Server server;

	/**
	 * Creates a new NetworkAdapter with no data available.
	 * @param server 
	 */
	public NetworkAdapter(Server s) {
		server = s;
		dataAvailable = false;
		connected = false;
		System.out.println("Adapter created");
	}

	/**
	 * Called from ConnectionListener. Signals that there is data available on
	 * the input stream.
	 */
	public void dataAvailable() {
		dataAvailable = true;

	}

	/**
	 * Connects to a given IP Address and port over the network.
	 * 
	 * @param IPAddress
	 *            The IP Address to connect to
	 * @param port
	 *            The port on the IP Address to connect to
	 * @return
	 * @throws IOException
	 *             If the connection fails. Usually signifies an incorrect
	 *             IPAddress and port configuration.
	 */
	public boolean connect(String IPAddress, int portNum) throws IOException {
		port = portNum;
		connection = new Socket(IPAddress, port);
		input = new DataInputStream(connection.getInputStream());
		ConnectionListener cL = new ConnectionListener(this, input);
		output = new DataOutputStream(connection.getOutputStream());
		cL.start();
		connected = true;
		return true;
	}

	/**
	 * Hosts a connection on the given port. To be able to properly connect
	 * outside of the LAN, the end-user much port forward this port to be able
	 * to communicate. Hamachi is a third-party program that can bypass this
	 * restriction, allowing another end-user to connect to the host without
	 * port-forwarding.
	 * 
	 * @param port
	 *            The port to host on
	 * @throws IOException
	 *             If the connection fails. Usually signifies a taken-port or
	 *             lack of host rights.
	 */
	public void host(int portNum) throws IOException {
		port = portNum;
		socket = new ServerSocket(port);
		socket.setSoTimeout(0); // Wait until cancelled
		System.out.println("Socket opened");

		hostThread = new HostThread(this, socket);
		hostThread.start();
	}

	ServerSocket socket;

	/**
	 * Returns true if there is data available in the input stream. Used to
	 * prevent constant attempts to read an empty input stream.
	 * 
	 * @return true if data is available in the input stream.
	 */
	public boolean isDataAvailable() {
		return dataAvailable;
	}

	/**
	 * Called after user handles all data available in the input stream. Sets
	 * dataAvailable to false until new data retriggers it to true.
	 */
	public void clearDataAvailable() {
		dataAvailable = false;
	}

	/**
	 * Returns the InputStream. Used to read data sent over the network.
	 * 
	 * @return input
	 */
	public DataInputStream getInputStream() {
		return input;
	}

	/**
	 * Returns the OutputStream. Used to send data over the network.
	 * 
	 * @return output
	 */
	public DataOutputStream getOutputStream() {
		return output;
	}

	ConnectionListener cL;

	public void connectionAvailable() {
		System.out.println("connection available.");
		input = hostThread.getInputStream();
		output = hostThread.getOutputStream();
		System.out.println("Pipes gotten.");
		cL = new ConnectionListener(this, input);
		cL.start();
		System.out.println("Listening");
		connected = true;
		hostThread.done = true;
		server.newConnection();
	}

	/**
	 * Returns the port being used by the Adapter.
	 * 
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Closes the connection.
	 * 
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		connection.close();
	}

	/**
	 * Stops listening for a connection.
	 */
	public void stopHosting() {
		// hostThread.interrupt();
		hostThread.setDone();
		cL.setDone();
	}

	public void rehost() {
		hostThread.setShouldRehost();
		stopHosting();
	}

	/**
	 * Returns true if connected to another computer.
	 * 
	 * @return True if connected
	 */
	public boolean isConnected() {
		System.out.println("Connected? " + connected); // For reasons I don't
														// understand, the
														// update will only work
														// more than once with
														// this here.
		return connected;
	}

	public void setConnected(boolean b) {
		connected = b;
	}

	/**
	 * 
	 */
	public void hostAvailable() {
		try {
			host(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void close() {
		try {
			socket.close();
			hostThread.setDone();
			hostThread = null;
			input.close();
			input = null;
			output.close();
			output = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
