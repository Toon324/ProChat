package proc;

/**
 * @author Cody
 *
 */
public class ProChat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Log.createWriter(); //Create debugger
			new LoginWindow();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
