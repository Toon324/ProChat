/**
 * 
 */
package proc.Voip;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Cody
 * 
 */
public class ConnectionListenerThread extends Thread {
	RecieveAudio master;
	ServerSocket server;

	public ConnectionListenerThread(RecieveAudio ra, ServerSocket s) {
		master = ra;
		server = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			Socket socket = server.accept();
			master.alertConnection(socket);

			// Log.l("Socket created");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
