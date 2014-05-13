package proc.Voip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Cody
 * 
 */
public class RelayServer {
	
	private TreeMap<String, String> map = new TreeMap<String, String>();

	public static void main(String[] args) {
		RelayServer rs = new RelayServer();
		try {
			rs.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean shouldRun = true;

	private void startServer() throws IOException, InterruptedException {
		JFrame frame = new JFrame("Relay Server");
		
		frame.setSize(400, 200);
		
		JTextArea text = new JTextArea();
		JScrollPane scroller = new JScrollPane(text);
		scroller.setAutoscrolls(true);
		frame.add(scroller);
		frame.setVisible(true);
		
		
		DatagramSocket serverSocket = new DatagramSocket(1324);
		byte[] receiveData = new byte[150];

		while (shouldRun) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			text.append("Listening...");
			serverSocket.receive(receivePacket);
			text.append("\nData: " + receivePacket.getData()[0] + " " + receivePacket.getData()[1]);
			String destination = new String(receivePacket.getData());
			String source = receivePacket.getSocketAddress() + "";
			source = source.replace("/", "");
			
			text.append("\nFrom: " + source + " To: " + destination);
			
			boolean placed = false;
			
			for (Entry<String, String> e : map.entrySet()) {
				if (source.contains(e.getKey())) {
					placed = true;
					map.put(source, e.getValue());
					map.remove(e.getKey());
					try {
						sendInfo(e);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if (source.contains(e.getValue())) {
					placed = true;
					e.setValue(source);
					try {
						sendInfo(e);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
			if (!placed)
				map.put(source, destination);
			
			printMap();

		}
		serverSocket.close();
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
		
		//Send IP2 (value) to IP1 (key)
		toSend = e.getValue().getBytes();
		packet.setData(toSend);
		packet.setAddress(InetAddress.getByName(e.getKey()));
		
		socket.send(packet);
		System.out.println("2->1: Sent " + new String(packet.getData()) + " to " + packet.getAddress()); 
		
		//Send IP1 to IP2
		toSend = e.getKey().getBytes();
		packet.setData(toSend);
		packet.setAddress(InetAddress.getByName(e.getValue()));
		System.out.println("1->2: Sent " + new String(packet.getData()) + " to " + packet.getAddress()); 
		
		socket.send(packet);
		
		socket.close();
		
	}

	/**
	 * 
	 */
	private void printMap() {
		for (Entry<String, String> e : map.entrySet())
			System.out.println("Entry found: " + e.getKey() + " " + e.getValue());
		
	}

	// private synchronized void sendPacket(DatagramSocket socket) throws
	// IOException{
	//
	// byte[] sendData = new byte[50];
	// ///sendData = data.getBytes();
	// DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
	// homeIPAddress, homePort);
	// socket.send(sendPacket);
	// }

}
