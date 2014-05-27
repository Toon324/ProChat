package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Cody
 *
 */
public class ServerHelper implements Runnable {
	Socket client;
	String name;
	Server server;
	
	public ServerHelper(Server s, Socket clientSocket, String n) {
		client = clientSocket;
		server = s;
		name = n;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
			
			boolean shouldRun = true;
			while (shouldRun) {
				
				String s = input.readLine();
				Scanner scan = new Scanner(s);
				scan.useDelimiter("\t");
				
				String cmd = scan.next();
				
				String arg = "";
				
				if (scan.hasNext())
					arg = scan.next();
				
				scan.close();
				
				System.out.println(name + ": Input: " + s);
				
				if (cmd.equals("VERSION")) {
					if (arg.equals("")) {
						System.out.println(name + ": No file specified.");
						client.close();
						input.close();
						output.close();
						shouldRun = false;
						return;
					}
					
					String version = server.getVersion(arg);
					System.out.println(name + ": Version of " + arg + " is " + version);
					output.write(version.getBytes());
					output.flush();
				}

				else if (cmd.equals("UPDATE")) {
					if (arg.equals("")) {
						System.out.println(name + ": No file specified.");
						client.close();
						input.close();
						output.close();
						shouldRun = false;
						return;
					}
					
					File updatedJar = new File(arg);
					output.writeInt(
							(int) updatedJar.length());
					output.flush(); // Let client
														// know how long
														// the file is
					System.out.println(name + ": Sending file of size "
							+ updatedJar.length());

					InputStream in = new FileInputStream(updatedJar);

					byte[] buf = new byte[(int) updatedJar.length()];
					int len;
					while ((len = in.read(buf)) > 0) {
						output.write(buf, 0, len);
					}
					in.close();
					System.out.println(name + ": Sent updated Jar.");
				}

				else if (cmd.equals("CLOSE")) {
					System.out.println(name + ": Closed connection.");
					client.close();
					shouldRun = false;
				}
			}
			
			input.close();
			output.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
