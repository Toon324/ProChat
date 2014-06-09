package proc.Voip;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import proc.Home;

/**
 * @author Cody
 *
 */
public class UDPTester {
	
	private final static String IP = "174.102.178.65";
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket sock = new Socket(IP, 1324);
		String testIP = "121.212.111.22";
		
		DataInputStream input = new DataInputStream(sock.getInputStream());
		PrintWriter output = new PrintWriter(sock.getOutputStream());
		
		output.write(IP);
		//output.write(testIP);
		output.flush();
		
		sock.close();
	}

}
