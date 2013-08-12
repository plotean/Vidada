package vlcj;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import vidada.views.directplay.IDecoupledRenderer;

/**
 * A pane on which a video is rendered.
 * 
 * Internally this encapsulates just an image, which is accessed directly by the video renderer and frames are put directly into it.
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class VideoSurfacePane extends JPanel {

	protected final BufferedImage image;
	protected final IDecoupledRenderer postRenderer;
	
	public VideoSurfacePane(BufferedImage image) {
		this(image, null);
	}
	
	public VideoSurfacePane(BufferedImage image, IDecoupledRenderer postRenderer) {
		assert image != null;
		
		this.image = image;
		this.postRenderer = postRenderer;
	}

	/**
	 * Get the buffered image holding the current frame
	 * @return
	 */
	public BufferedImage getImage(){
		return image;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		// draw the video frame
		g2.drawImage(image, null, 0, 0);

		// allow post renderer to draw an overlay
		if(postRenderer != null){
			postRenderer.render(g2);
		}
	}

}