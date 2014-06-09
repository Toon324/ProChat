package proc.Voip;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Cody
 * 
 */
public class RelayHelper implements Runnable {

	private RelayServer server;
	private Socket client;
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
			DataOutputStream output = new DataOutputStream(
					client.getOutputStream());

			IPMap<String, String> map = server.getMap();

			boolean shouldRun = true;
			while (shouldRun) {
				if (input.ready()) {
					String otherIP = input.readLine();
					String ownIP = client.getInetAddress().toString().replace("/", "");
					ownIP = ownIP + ":" + client.getLocalPort();
					
					String searchIP = "";
					if (otherIP.contains(":"))
						searchIP = otherIP.substring(0, otherIP.indexOf(":"));
					else
						searchIP = otherIP;

					System.out.println(name + ": A (" + ownIP
							+ ") is requesting connection to B (" + otherIP
							+ ") (Search: " + searchIP + ")");

					if (map.containsKey(searchIP)) {
						String match = map.get(searchIP);
						System.out.println(name
								+ ": Map already had otherip. Matched IP: " + match);
						sendInfo(ownIP, match);
					} else {
						map.put(ownIP, otherIP);
						System.out.println(name + ": New entry created.");
					}
					server.printMap();
				}
			}
			input.close();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param e
	 * @throws SocketException
	 */
	private void sendInfo(String A, String B) throws Exception {
		byte[] toSend = new byte[50];
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length);
		packet.setPort(1324);
		DatagramSocket socket = new DatagramSocket();
		
		String ip1 = B;
		String ip2 = A;
		
		if (ip1.contains(":"))
			ip1 = ip1.substring(0, ip1.indexOf(":"));
		
		if (ip2.contains(":"))
			ip2 = ip2.substring(0, ip2.indexOf(":"));

		// Send IP2 (A) to IP1 (B)
		toSend = A.getBytes();
		packet.setData(toSend);
		packet.setAddress(InetAddress.getByName(ip1));

		socket.send(packet);
		System.out.println("2->1: Sent " + new String(packet.getData())
				+ " to " + packet.getAddress());

		// Send IP1 to IP2
		toSend = B.getBytes();
		packet.setData(toSend);
		packet.setAddress(InetAddress.getByName(ip2));
		System.out.println("1->2: Sent " + new String(packet.getData())
				+ " to " + packet.getAddress());

		socket.send(packet);

		socket.close();

	}
}
