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
			PrintWriter output = new PrintWriter(client.getOutputStream());

			TreeMap<String, String> map = server.getMap();
			TreeMap<String, String> searches = server.getSearches();

			boolean shouldRun = true;
			while (shouldRun) {
				if (input.ready()) {
					String caller = input.readLine();
					String callerIP = client.getInetAddress().toString().replace("/", "");
					String reciever = input.readLine();
					
					//Add this Name-IP pairing to our "Phonebook"
					map.put(caller, callerIP);
					
					if (searches.containsKey(reciever) && searches.get(reciever).equals(caller)) {
						System.out.println("Found search match: " + reciever + " looking for " + searches.get(reciever));
						shareInfo(caller, reciever);
						output.write(map.get(reciever));
					}
					else
						searches.put(caller, reciever);
					
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
	 * @param caller
	 * @param reciever
	 */
	private void shareInfo(String caller, String reciever) {
		// TODO Auto-generated method stub
		try {
			Socket sock = new Socket(server.getMap().get(reciever), 1324);
			PrintWriter output = new PrintWriter(sock.getOutputStream());
			output.write(server.getMap().get(caller));
			
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
