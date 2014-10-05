package proc.Voip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import proc.Log;

//Author: Cody

public class VoiceCall {

	private final static String relayServerIP = "54.200.92.207";
	ExecutorService threadPool;
	static int bufferSize = 8002;
	AudioCapture ac;
	RecieveAudio ra;
	int externalPort = 0;

	public static void main(String[] args) {
		//new VoiceCall("Alice", "Bob");
		new VoiceCall("Bob", "Alice");
	}

	public VoiceCall(String caller, String recipient) {

		threadPool = Executors.newCachedThreadPool();

		Log.l(caller + " is calling " + recipient);
		
		String ip;
		try {
			ip = fetchIP(caller, recipient);
			
			//String hostIP = ip.substring(0, ip.indexOf(":"));
			//int port = Integer.valueOf(ip.substring(ip.indexOf(":") + 1, ip.length()));
			
			DatagramSocket comms = new DatagramSocket(externalPort);
			System.out.println("Opened datagram socket on port " + externalPort);
			
			ac = new AudioCapture(comms, ip, threadPool);

			ra = new RecieveAudio(comms, threadPool);
			ra.playAudio(); // Listen for audio
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ac.captureAudio(); //Capture

	}

	/**
	 * @param caller
	 * @param recipient
	 * @return
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private String fetchIP(String caller, String recipient) throws UnknownHostException, IOException {
		Socket sock = new Socket(relayServerIP, 1324);
		
		BufferedReader input = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));
		PrintWriter output = new PrintWriter(sock.getOutputStream());
		
		output.write(caller +"\n");
		output.write(recipient + "\n");
		output.flush();
		
		System.out.println("Made call request.");
		
		boolean shouldRun = true;
		
		String recieverIP = "";
		
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
		
		return recieverIP;
	}

	public static String fetchExternalIP() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));

			String ip = in.readLine(); // you get the IP as a String
			return ip;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public ExecutorService getPool() {
		return threadPool;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @return the ac
	 */
	public AudioCapture getCapture() {
		return ac;
	}

	/**
	 * @return the ra
	 */
	public RecieveAudio getRecieve() {
		return ra;
	}

}
