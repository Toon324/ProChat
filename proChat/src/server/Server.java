package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Cody Swendrowski
 * 
 */
public class Server {
	private static double version = 1.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Server has started.");
		loadVersionData();
		NetworkAdapter adapter = new NetworkAdapter();

		try {
			adapter.host(60);
			System.out.println("Server is now hosting");
		} catch (Exception e) {
		}

		while (true) {
			try {

				long timeIn = System.currentTimeMillis();
				while (!adapter.isConnected()) {
					long timeOut = System.currentTimeMillis();
					while (timeOut - timeIn <= 100)
						timeOut = System.currentTimeMillis();
				} // Do nothing until Connected

				System.out.println("Server has made a connection.");
				while (adapter.isConnected()) {

					System.out.println("Server is running.");

					if (adapter.isDataAvailable()) {
						System.out.println("Data available.");
						int input = adapter.getInputStream().readInt();

						System.out.println("Call = " + input);

						if (input == 0) {
							adapter.getOutputStream().writeDouble(version);
							adapter.clearDataAvailable();
						}

						else if (input == 1) {
							File updatedJar = new File("ProChatAlpha.jar");
							System.out.println("Got updatedJar.");
							adapter.getOutputStream().writeInt(
									(int) updatedJar.length());
							adapter.getOutputStream().flush(); // Let client
																// know how long
																// the file is

							InputStream in = new FileInputStream(updatedJar);

							byte[] buf = new byte[(int) updatedJar.length()];
							int len;
							while ((len = in.read(buf)) > 0) {
								adapter.getOutputStream().write(buf, 0, len);
							}
							in.close();
							adapter.clearDataAvailable();
						}

						else if (input == 2)
							adapter.setConnected(false);
						
					} else {
						System.out.println("No data");
					}

					timeIn = System.currentTimeMillis();
					long timeOut = System.currentTimeMillis();
					while (timeOut - timeIn <= 100)
						timeOut = System.currentTimeMillis();

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private static void loadVersionData() {
		File file = new File("serverVersionID.txt");
		try {
			if (!file.exists()) { // If file doesn't exist, create one
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write("1.0");
				writer.close();
			}

			Scanner scanner = new Scanner(file);

			version = scanner.nextDouble();
			
			System.out.println("Found server version: " + version);

			scanner.close();

		} catch (IOException e) {
		}
	}

}
