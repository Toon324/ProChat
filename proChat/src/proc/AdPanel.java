package proc;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * @author Cody
 * 
 */
public class AdPanel extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5913790930853170631L;

	private final int LIFESPAN = 30000;
	private final int AMT = 6;
	private BufferedImage images[] = new BufferedImage[AMT];
	private String urls[] = new String[AMT];
	private int currentIndex;

	public AdPanel() {
		loadResources();

		Random gen = new Random();
		currentIndex = gen.nextInt(AMT);

		addMouseListener(this);

		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.setMinimumSize(new Dimension(200, 150));

		setSize(new Dimension(200, 150));

		ProChat.createNewThread(new Runnable() {

			@Override
			public synchronized void run() {
				while (true) {
					try {
						this.wait(LIFESPAN);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					cycleAd();
				}
			}

		});

	}

	private void loadResources() {
		images[0] = ProChat.loadImage("testAd.png");

		images[1] = ProChat.loadImage("ad1.png");
		urls[1] = "https://www.youtube.com/channel/UC-HlnqqvEHYZWJ6IGbDcuXQ";

		images[2] = ProChat.loadImage("ad2.png");
		urls[2] = "https://www.youtube.com/channel/UC-HlnqqvEHYZWJ6IGbDcuXQ";

		images[3] = ProChat.loadImage("ad3.png");
		urls[3] = "mailto:cody@swendrowski.us";

		images[4] = ProChat.loadImage("ad4.png");

		images[5] = ProChat.loadImage("ad5.png");
		urls[5] = "http://www.reddit.com/r/onetruegod";
	}

	private void cycleAd() {
		Random gen = new Random();

		int temp = gen.nextInt(AMT);
		while (temp == currentIndex)
			temp = gen.nextInt(AMT);

		currentIndex = temp;
		// Log.l("Now displaying ad " + currentIndex);
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// System.out.println(getWidth() + "  " + getHeight());
		g.drawImage(images[currentIndex], 0, 0, getWidth(), getHeight(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		if (urls[currentIndex] == null)
			return;

		try {
			handleClick();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void handleClick() throws URISyntaxException, IOException {
		URI myURI = new URI(urls[currentIndex]);
		if (urls[currentIndex].contains("mailto"))
			Desktop.getDesktop().mail(myURI);
		else
			Desktop.getDesktop().browse(myURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
