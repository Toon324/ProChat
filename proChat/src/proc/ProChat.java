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
		Home home = new Home();
		home.show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
