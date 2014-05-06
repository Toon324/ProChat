package proc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Cody
 *
 */
public class SavedInfoLoader {
	private final String fileName = "savedInfo.txt";
	private static SavedInfoLoader instance;
	private StringBuilder toWrite;
	private File saved;
	
	String user = "";
	String pass = "";
	String status;
	
	public SavedInfoLoader() {
		toWrite = new StringBuilder();
		
		saved = new File(fileName);
		if (!saved.exists())
			try {
				saved.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else {
			try {
				// Load in the saved login info
				Scanner reader = new Scanner(saved);
				//reader.useDelimiter("\t");
				while (reader.hasNextLine()) {
					String found = reader.nextLine();
					toWrite.append(found + "\r\n");
					Log.l("Line: " + found);
					
					handleLine(reader, found);
				}
				reader.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @param reader 
	 * @param found
	 */
	private void handleLine(Scanner reader, String found) {
		if (found.equals("[LOGIN]"))
		{
			String login = reader.nextLine();
			Scanner lineReader = new Scanner(login);
			lineReader.useDelimiter("\t");
			
			while (lineReader.hasNext()) {
				user = lineReader.next();
				pass = lineReader.next();
			}
			lineReader.close();
		}
		else if (found.equals("[STATUS]"))
			status = reader.nextLine();
		
	}

	/**
	 * 
	 */
	public static void createInstance() {
		instance = new SavedInfoLoader();
		
	}

	/**
	 * @return
	 */
	public static SavedInfoLoader getInstance() {
		return instance;
	}

	/**
	 * @param string
	 */
	public void updateSavedLogin(String newLogin) {
		toWrite.replace(toWrite.indexOf("[LOGIN]"), toWrite.indexOf("[STATUS]"), "[LOGIN]\r\n" + newLogin + "\r\n");
		
		write();
		
	}

	/**
	 * 
	 */
	private void write() {
		try {
			PrintWriter writer = new PrintWriter(saved);
			writer.write(toWrite.toString());
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @param s
	 */
	public void updateStatus(String s) {
		toWrite.replace(toWrite.indexOf("[STATUS]"), toWrite.length(), "[STATUS]\r\n" + s + "\r\n");
		write();
	}
	
	

}
