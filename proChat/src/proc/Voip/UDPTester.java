package proc.Voip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Cody
 *
 */
public class UDPTester {
	
	private final static String IP = "54.200.92.207";
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket sock = new Socket(IP, 1324);
		
		BufferedReader input = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));
		PrintWriter output = new PrintWriter(sock.getOutputStream());
		
		output.write("Alice\n");
		output.write("Bob\n");

//		output.write("Bob\n");
//		output.write("Alice\n");
		
		output.flush();
		
		boolean shouldRun = true;
		
		String recieverIP = "";
		int externalPort = 0;
		
		System.out.println("Waiting on server response.. ");
		while (shouldRun) {
			if (input.ready()) {
				String s = input.readLine();
				System.out.println("Recieved: " + s);
				String p = input.readLine();
				System.out.println("Port: " + p);
				externalPort = Integer.valueOf(p);
				recieverIP = s;
				shouldRun = false;
			}
		}
		
		sock.close();
		
		String ip = recieverIP.substring(0, recieverIP.indexOf(":"));
		int port = Integer.valueOf(recieverIP.substring(recieverIP.indexOf(":") + 1, recieverIP.length()));
		
		DatagramSocket comms = new DatagramSocket(externalPort);
		System.out.println("Opened datagram socket on port " + externalPort);
		
		byte[] toSend = new byte[50];
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length);
		packet.setPort(port);
		packet.setAddress(InetAddress.getByName(ip));
		
		packet.setData("Hello!".getBytes());
		
		comms.send(packet);
		System.out.println("Sent hello.");

		comms.receive(packet);
		System.out.println("Recieved: " + new String(packet.getData()));
		
		packet.setData("Goodbye.".getBytes());
		
		comms.send(packet);
		System.out.println("Sent goodbye.");

		comms.close();
	}

}
