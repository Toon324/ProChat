package proc;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

/**
 * @author Cody
 *
 */
public class ProChat {
	
	private static LoginWindow login;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Log.createWriter(); //Create debugger
			login =new LoginWindow();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage loadImage(String imagePath) {
		try {
			return ImageIO.read(login.getClass().getResourceAsStream(
					imagePath));
		} catch (IOException e) {
			Log.e("Could not load image " + imagePath + " due to " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static void createNewThread(Runnable runnable) {
		Executors.newCachedThreadPool().execute(runnable);	
	}

}
