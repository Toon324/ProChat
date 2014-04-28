package updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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
		frame.setSize(400,400);
		frame.setVisible(true);
		
		text.setText("Local version: " + version);

		NetworkAdapter adapter = new NetworkAdapter();
		try {
			adapter.connect("129.89.185.223", 1160);
			text.append("\nConnected to server at 129.89.185.223:1160.");
			adapter.getOutputStream().writeInt(0); // Request for version
			adapter.getOutputStream().flush();

			String serverVersion = "no data";
			while (serverVersion.equals("no data")) {
				try {
				System.out.println(serverVersion);
				if (adapter.isDataAvailable()) {
					text.append("\n Server version: " + serverVersion);
					StringBuilder builder = new StringBuilder();
					while (adapter.getInputStream().available() > 0)
						builder.append(adapter.getInputStream().readChar());
					
					System.out.println("Appending: " + builder.toString());
					serverVersion = builder.toString();
					
				}
				Thread.sleep(100);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			text.append("\nServer Version: " + serverVersion);

			if (serverVersion.compareTo(version) > 0) {
				text.append("\nUpdating from " + version + " to "
						+ serverVersion);
				adapter.getOutputStream().writeInt(1); // Request for updated
														// jar

				BufferedInputStream bufIn = new BufferedInputStream(
						adapter.getInputStream());

				File fileWrite = new File("ProChatAlpha.jar");
				OutputStream out = new FileOutputStream(fileWrite);
				BufferedOutputStream bufOut = new BufferedOutputStream(out);

				byte buffer[] = new byte[adapter.getInputStream().readInt()];

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
					//System.out.println("Total: " + wrote);
					text.append("\n" + ((int)(100*(wrote/(double)buffer.length))) + "%");
					if (wrote == buffer.length)
						complete = true;
				}
				text.append("\nClosing resources..");
				bufOut.flush();
				out.close();
				text.append("\nFinished writing data.");

				adapter.getOutputStream().writeInt(2); // Tell the server that
														// the client is done
														// with it
				text.append("\nUpdating local info..");
				updateVersionIDTo(serverVersion);
				
				text.append("\nUpdated. Now launching...");

				Runtime.getRuntime().exec(
						"java -jar ProChatAlpha.jar");
				
				frame.dispose();
			} else {
				adapter.getOutputStream().writeInt(2);
				Runtime.getRuntime().exec("java -jar ProChatAlpha.jar");
			}
		} catch (IOException e) {
			
			text.append("\nERROR: Could not connect to server.");
			text.append(e.getMessage());
			try {
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
				writer.write("0.0");
				writer.close();
			}

			Scanner scanner = new Scanner(file);

			version = scanner.next();

			scanner.close();

		} catch (IOException e) {
		}
	}

}
