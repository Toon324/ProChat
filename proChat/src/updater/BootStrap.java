package updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Checks the current version of the program against a server, then proceeds to
 * update as necessary.
 * 
 * @author Cody Swendrowski
 */
public class BootStrap {

	static double version = 0.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadVersionData();
		System.out.println("Version: " + version);

		NetworkAdapter adapter = new NetworkAdapter();
		try {
			adapter.connect("129.89.185.120", 60);
			System.out.println("Connected to server.");
			adapter.getOutputStream().writeInt(0); // Request for version
			adapter.getOutputStream().flush();

			double serverVersion = 0.0;
			while (serverVersion == 0.0) {
				if (adapter.isDataAvailable()) {
					serverVersion = adapter.getInputStream().readDouble();
				}
				while (System.currentTimeMillis() % 100 != 0) {
				} // wait
			}

			System.out.println("Server Version: " + serverVersion);

			if (serverVersion > version) {
				System.out.println("Updated from " + version + " to "
						+ serverVersion);
				adapter.getOutputStream().writeInt(1); // Request for updated
														// jar

				BufferedInputStream bufIn = new BufferedInputStream(
						adapter.getInputStream());

				File fileWrite = new File("ProChatAlpha.jar");
				OutputStream out = new FileOutputStream(fileWrite);
				BufferedOutputStream bufOut = new BufferedOutputStream(out);

				byte buffer[] = new byte[adapter.getInputStream().readInt()];

				while (true) {
					int nRead = bufIn.read(buffer, 0, buffer.length);
					if (nRead <= 0)
						break;
					bufOut.write(buffer, 0, nRead);
				}

				bufOut.flush();
				out.close();

				adapter.getOutputStream().writeInt(2); // Tell the server that
														// the client is done
														// with it
				updateVersionIDTo(serverVersion);

				Runtime.getRuntime().exec(
						"java -jar ProChatAlpha.jar");
			} else
				Runtime.getRuntime().exec("java -jar ProChatAlpha.jar");
		} catch (IOException e) {
			System.out.println("ERROR: Could not connect to server.");
			try {
				Runtime.getRuntime().exec("java -jar EmployeeEvalSystem.jar");
			} catch (IOException e1) {
			}
		}
	}

	private static void updateVersionIDTo(double serverVersion) {
		File file = new File("src\\versionID.txt");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("" + serverVersion);
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
				writer.write("1.0");
				writer.close();
			}

			Scanner scanner = new Scanner(file);

			version = scanner.nextDouble();

			scanner.close();

		} catch (IOException e) {
		}
	}

}
