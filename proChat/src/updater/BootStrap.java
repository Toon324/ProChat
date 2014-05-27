package updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Checks the current version of the program against a server, then proceeds to
 * update as necessary.
 * 
 * @author Cody Swendrowski
 */
public class BootStrap {

	private static final String IP = "174.102.178.65";
	private static final int PORT = 1160;
	static String version = "0.0.0";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadVersionData();

		JFrame frame = new JFrame();
		JTextArea text = new JTextArea();
		JScrollPane scroller = new JScrollPane(text);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(scroller);
		frame.setSize(400, 400);
		frame.setVisible(true);

		text.setText("Local version: " + version);

		try {
			System.out.println("Attempting connection to " + IP + ":" + PORT);
			Socket server = new Socket(IP, PORT);
			DataInputStream input = new DataInputStream(server.getInputStream());
			PrintWriter output = new PrintWriter(server.getOutputStream());
			text.append("\nConnected to server at " + IP + ":" + PORT);
			output.write("VERSION\tProChatAlpha.jar\n"); // Request for version
			output.flush();

			String serverVersion = "no data";
			while (serverVersion.equals("no data")) {
				try {
					System.out.println(serverVersion);
					if (input.available() > 0) {
						text.append("\n Server version: " + serverVersion);
						byte[] b = new byte[40];
						input.read(b);
						serverVersion = new String(b);

					}
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			text.append("\nServer Version: " + serverVersion);

			// System.out.println(serverVersion.compareTo(version));

			if (serverVersion.compareTo(version) > 0) {
				text.append("\nUpdating from " + version + " to "
						+ serverVersion);
				output.write("UPDATE\tProChatAlpha.jar\n"); // Request for
															// updated
				output.flush();
				// jar

				BufferedInputStream bufIn = new BufferedInputStream(input);

				File fileWrite = new File("ProChatAlpha.jar");
				OutputStream out = new FileOutputStream(fileWrite);
				BufferedOutputStream bufOut = new BufferedOutputStream(out);

				byte buffer[] = new byte[input.readInt()];

				text.append("\nRecieving file of size " + buffer.length);
				boolean complete = false;
				int wrote = 0;
				while (!complete) {
					int nRead = bufIn.read(buffer, 0, buffer.length);
					if (nRead <= 0) {
						complete = true;
						System.out.println("Complete!");
					}
					bufOut.write(buffer, 0, nRead);
					wrote += nRead;
					// System.out.println("Total: " + wrote);
					text.append("\n"
							+ ((int) (100 * (wrote / (double) buffer.length)))
							+ "%");
					if (wrote == buffer.length)
						complete = true;
				}

				output.write("CLOSE\n"); // Tell the server that the client is
				// done with it
				output.flush();

				text.append("\nClosing resources..");
				bufOut.flush();
				out.close();
				server.close();
				text.append("\nFinished writing data.");

				text.append("\nUpdating local info..");
				updateVersionIDTo(serverVersion);

				text.append("\nUpdated. Now launching...");

				Runtime.getRuntime().exec("java -jar ProChatAlpha.jar");

				frame.dispose();
			} else {
				output.write("CLOSE\n");
				output.flush();
				frame.dispose();
				Runtime.getRuntime().exec("java -jar ProChatAlpha.jar");
			}
		} catch (IOException e) {

			text.append("\nERROR: Could not connect to server.");
			text.append(e.getMessage());
			try {
				frame.dispose();
				Runtime.getRuntime().exec("java -jar EmployeeEvalSystem.jar");
			} catch (IOException e1) {
				text.append("ERROR: Could not launch the program.");
			}
		}
	}

	private static void updateVersionIDTo(String serverVersion) {
		File file = new File("src\\versionID.txt");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(serverVersion);
			writer.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 */
	private static void loadVersionData() {
		File file = new File("src\\versionID.txt");
		try {
			if (!file.exists()) { // If file doesn't exist, create one
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write("0.0.0");
				writer.close();
			}

			Scanner scanner = new Scanner(file);

			version = scanner.next();

			scanner.close();

		} catch (IOException e) {
		}
	}

}
