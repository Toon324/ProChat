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
			
			threadPool.execute(new RelayHelper(this, client, "Helper" + cnt));
			System.out.println("Created helper iteration " + cnt);
			cnt++;
			printMap();
		}

		server.close();
	}

	/**
	 * 
	 */
	private void printMap() {
		for (Entry<String, String> e : map.entrySet())
			System.out.println("Entry found: " + e.getKey() + " "
					+ e.getValue());

	}
	
	public TreeMap<String, String> getMap() {
		return map;
	}

}
