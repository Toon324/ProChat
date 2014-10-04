package proc.Voip;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Cody
 * 
 */
public class RelayHelper implements Runnable {

	private RelayServer server;
	 Socket client;
	private String name;

	/**
	 * @param relayServer
	 * @param client
	 * @param string
	 */
	public RelayHelper(RelayServer rs, Socket c, String n) {
		server = rs;
		client = c;
		name = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			PrintWriter output = new PrintWriter(client.getOutputStream());

			TreeMap<String, String> map = server.getMap();
			TreeMap<String, String> searches = server.getSearches();

			boolean shouldRun = true;
			while (shouldRun) {
				if (input.ready()) {
					String caller = input.readLine();
					int clientPort = client.getPort();
					String callerIP = client.getInetAddress().toString()
							.replace("/", "")
							+ ":" + clientPort;
					
					String reciever = input.readLine();

					// Add this Name-IP pairing to our "Phonebook"
					map.put(caller, callerIP);

					if (searches.containsKey(reciever)
							&& searches.get(reciever).equals(caller)) {
						System.out.println("Found search match: " + reciever
								+ " looking for " + searches.get(reciever));
						System.out.println(reciever + " has IP "
								+ map.get(reciever) + " and " + caller
								+ " has IP " + map.get(caller));

						output.write(map.get(reciever) + "\n");
						output.write(clientPort + "\n");
						output.flush();

						input.close();
						output.close();
						
						searches.remove(reciever);
						
						server.resolveSocket(client.getInetAddress() + ":" + client.getPort());

						System.out.println("Shared recieverIP with caller.");
						shareInfo(caller, reciever);
						
						shouldRun = false;
					} else
						searches.put(caller, reciever);

					server.printMap();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param caller
	 * @param reciever
	 */
	private void shareInfo(String caller, String reciever) {
		// TODO Auto-generated method stub
		try {
			String fullip = server.getMap().get(reciever);
			String ip = fullip.substring(0, fullip.indexOf(":"));
			int port = Integer.valueOf(fullip.substring(
					fullip.indexOf(":") + 1, fullip.length()));

			System.out.println("Fetching socket for " + ip);
			
			fullip = "/" + fullip;

			if (!server.getSockets().containsKey(fullip)) {
				System.out.println("ERROR: No socketmap for " + fullip);
				server.printMap();
				return;
			}
			
			Socket sock = server.getSockets().get(fullip).client;

			System.out.println("Complete.");

			PrintWriter output = new PrintWriter(sock.getOutputStream());
			output.write(server.getMap().get(caller) + "\n");
			output.write(port + "\n");
			output.flush();
			
			server.resolveSocket(fullip);

			System.out.println("Shared Caller IP with reciever.");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
