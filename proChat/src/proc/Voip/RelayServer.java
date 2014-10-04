package proc.Voip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import proc.Home;

/**
 * @author Cody
 * 
 */
public class RelayServer {

	private TreeMap<String, String> map = new TreeMap<String, String>();
	private TreeMap<String, String> searches = new TreeMap<String, String>();
	
	private TreeMap<String, RelayHelper> sockets = new TreeMap<String, RelayHelper>();

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

	private ExecutorService threadPool;
	private int cnt = 0;

	private void startServer() throws IOException, InterruptedException {
		threadPool = Executors.newCachedThreadPool();

		boolean shouldRun = true;

		ServerSocket server = new ServerSocket(1324);
		System.out.println("Server has started at " + Home.getIP() + ":1324");

		while (shouldRun) {
			Socket client = null;

			try {
				System.out.println("\nServer is waiting for a new connection.");
				client = server.accept();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			RelayHelper rh = new RelayHelper(this, client, "Helper" + cnt);
			
			sockets.put(client.getInetAddress().toString() + ":" + client.getPort(), rh);
			
			threadPool.execute(rh);
			System.out.println("Created helper iteration " + cnt);
			cnt++;
			printMap();
		}

		server.close();
	}

	/**
	 * 
	 */
	void printMap() {
		for (Entry<String, String> e : map.entrySet())
			System.out.println("\nIP mapping found: " + e.getKey() + " "
					+ e.getValue());
		for (Entry<String, String> e : searches.entrySet())
			System.out.println("Search map has: " + e.getKey() + " "
					+ e.getValue());
		for (Entry<String, RelayHelper> e : sockets.entrySet())
			System.out.println("Sockets map has: " + e.getKey() + " "
					+ e.getValue());
	}
	
	public TreeMap<String, String> getMap() {
		return map;
	}

	public TreeMap<String, String> getSearches() {
		return searches;
	}
	
	public TreeMap<String, RelayHelper> getSockets() {
		return sockets;
	}
	
	public void resolveSocket(String ip) {
		if (!sockets.containsKey(ip)) {
			System.out.println("ERROR: No socket mapping with " + ip);
			printMap();
			return;
		}
		try {
			if (sockets.get(ip).client != null)
					sockets.get(ip).client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sockets.remove(ip);
	}
}
