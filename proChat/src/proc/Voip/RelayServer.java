package proc.Voip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map.Entry;
import java.util.TreeMap;

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
		DatagramSocket serverSocket = new DatagramSocket(1324);
		byte[] receiveData = new byte[150];

		while (shouldRun) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			System.out.println("Listening...");
			serverSocket.receive(receivePacket);
			System.out.println("Data: " + receivePacket.getData()[0] + " " + receivePacket.getData()[1]);
			String destination = new String(receivePacket.getData());
			String source = receivePacket.getSocketAddress() + "";
			source = source.replace("/", "");
			
			System.out.println("From: " + source + " To: " + destination);
			
			boolean placed = false;
			
			for (Entry<String, String> e : map.entrySet()) {
				if (source.contains(e.getKey())) {
					placed = true;
					map.put(source, e.getValue());
					map.remove(e.getKey());
				}
				else if (source.contains(e.getValue())) {
					placed = true;
					e.setValue(source);
				}
			}
			
			if (!placed)
				map.put(source, destination);
			
			printMap();

		}
		serverSocket.close();
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
