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

			TreeMap<String, String> map = server.getMap();

			boolean shouldRun = true;
			while (shouldRun) {
				if (input.ready()) {
					String otherIP = input.readLine();
					String ownIP = client.getInetAddress().toString().replace("/", "");

					System.out.println(name + ": A (" + ownIP
							+ ") is requesting connection to B (" + otherIP
							+ ")");

					if (map.containsKey(otherIP)) {
						map.put(otherIP, ownIP);
						System.out.println(name
								+ ": Map already had otherip. Matching IPs.");
					} else {
						map.put(ownIP, otherIP);
						System.out.println(name + ": New entry created.");
					}
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
	private void sendInfo(Entry<String, String> e) throws Exception {
		byte[] toSend = new byte[50];
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length);
		packet.setPort(1324);
		DatagramSocket socket = new DatagramSocket();

		// Send IP2 (value) to IP1 (key)
		toSend = e.getValue().getBytes();
		packet.setData(toSend);
		packet.setAddress(InetAddress.getByName(e.getKey()));

		socket.send(packet);
		System.out.println("2->1: Sent " + new String(packet.getData())
				+ " to " + packet.getAddress());

		// Send IP1 to IP2
		toSend = e.getKey().getBytes();
		packet.setData(toSend);
		packet.setAddress(InetAddress.getByName(e.getValue()));
		System.out.println("1->2: Sent " + new String(packet.getData())
				+ " to " + packet.getAddress());

		socket.send(packet);

		socket.close();

	}
}
