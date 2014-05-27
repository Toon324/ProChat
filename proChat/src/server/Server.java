package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Cody Swendrowski
 * 
 */
public class Server {
	private TreeMap<String, String> infoMap = new TreeMap<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Server();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private ExecutorService threadPool;
	private int cnt = 0;

	public Server() throws IOException {
		threadPool = Executors.newCachedThreadPool();

		loadVersionData();

		boolean shouldRun = true;

		ServerSocket server = new ServerSocket(1160);
		System.out.println("Server has started at " + server.getInetAddress());

		while (shouldRun) {
			Socket client = null;
			
			try {
				//System.out.println("\nServer is waiting for a new connection.");
				client = server.accept();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			threadPool.execute(new ServerHelper(this, client, "Helper" + cnt ));
			System.out.println("Created helper iteration " + cnt);
			cnt++;
			
			//Refresh version numbers every 25 connections
			if (cnt % 25 == 0)
				loadVersionData();
		}
		
		server.close();
	}

	/**
	 * 
	 */
	public void loadVersionData() {
		File file = new File("serverVersionID.txt");
		try {
			if (!file.exists()) { // If file doesn't exist, create one
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write("serverVersionID.txt\t0.0.0");
				writer.close();
			}

			Scanner scanner = new Scanner(file);
			
			while (scanner.hasNextLine()) {
				Scanner lineScan = new Scanner(scanner.nextLine());
				lineScan.useDelimiter("\t");
				
				String name = lineScan.next();
				String ver = lineScan.next();
				lineScan.close();
				
				infoMap.put(name, ver);
				System.out.println("Found server version " + ver + " of file " + name);
			}

			scanner.close();

		} catch (IOException e) {
		}
	}

	/**
	 * @param arg
	 * @return
	 */
	public String getVersion(String arg) {
		return infoMap.get(arg);
	}

}
