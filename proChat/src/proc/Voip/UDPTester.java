package proc.Voip;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import proc.Home;

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
		
		output.write("Alice");
		output.write("Bob");

//		output.write("Bob");
//		output.write("Alice");
		
		output.flush();
		
		boolean shouldRun = true;
		
		System.out.println("Waiting on server response.. ");
		while (shouldRun) {
			if (input.ready()) {
				String s = input.readLine();
				System.out.println("Recieved: " + s);
				shouldRun = false;
			}
		}
		
		sock.close();
	}

}
