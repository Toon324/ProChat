/**
 * 
 */
package proc;

/**
 * @author Cody
 *
 */
public class User {
	String userName, userPass;
	
	public User (String name, String pass) {
		userName = name;
		userPass = pass;
	}
	
	public String getName() {
		return userName;
	}
	
	public String getPass() {
		return userPass;
	}
}
