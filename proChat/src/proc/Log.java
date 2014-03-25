package proc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * @author Cody
 * 
 */
public class Log {
	static PrintWriter writer;
	static File output;

	public static void createWriter() {
		try {
			output = new File("debug.txt");
			output.delete();
			output.createNewFile();
			writer = new PrintWriter(new FileOutputStream(output, true));
		} catch (Exception e) {
			e.printStackTrace();
			e(e.getMessage());
		}
	}

	public static void l(String s) {
		System.out.println(s);

		try {
			writer = new PrintWriter(new FileOutputStream(output, true));
			writer.write(s);
			writer.println();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void e(String s) {
		l("ERROR: " + s);
	}
}
