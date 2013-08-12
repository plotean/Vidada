package vidada.views.directplay;

import javax.swing.JPanel;

import archimedesJ.util.OSValidator;

/**
 * A panel which is able to render a video upon
 * 
 * @author IsNull
 * 
 */
@SuppressWarnings("serial")
public abstract class DirectPlayBaseComponent extends JPanel {

	private static boolean USE_ALLWAYS_DIRECT_DRAW = true;
	private volatile IDecoupledRenderer videoOverlayRenderer;

	/**
	 * builds an OS specific direct player if possible
	 * 
	 * @return
	 */
	public static DirectPlayBaseComponent build() {
		if (USE_ALLWAYS_DIRECT_DRAW)
			return buildDirectDrawPlayer();
		return OSValidator.isOSX() ? buildDirectDrawPlayer() : new DirectPlayComponentNative();
	}

	public static DirectDrawPreviewer buildDirectDrawPlayer() {
		if (OSValidator.isHDPI()) {
			System.out.println("using HDPI DirectPlay player");
			return new DirectDrawPreviewerHDPI();
		} else {
			System.out.println("using normal DPI DirectPlay player");
			return new DirectDrawPreviewer();
		}
	}

	/**
	 * set aspect ration and zoom
	 * 
	 * @param aspectRatio
	 */
	public abstract void setCropGeometry(String aspectRatio);

	public abstract void setScale(float factor);

	/**
	 * Play the given media file in this player
	 * 
	 * @param file
	 */
	public abstract void playMedia(String file);

	/**
	 * Set the player position to the given relative
	 * 
	 * @param pos
	 */
	public abstract void setPosition(float pos);

	/**
	 * Get the players current relative position
	 * 
	 * @return
	 */
	public abstract float getPosition();

	/**
	 * Stop the playback
	 */
	public abstract void stop();

	/**
	 * Set a video overlay renderer
	 * 
	 * @param videoOverlayRenderer
	 */
	public void setVideoOverlayRenderer(IDecoupledRenderer videoOverlayRenderer) {
		this.videoOverlayRenderer = videoOverlayRenderer;
	}

	public IDecoupledRenderer getVideoOverlayRenderer() {
		return videoOverlayRenderer;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		ensurePlayerSize();
	}

	public abstract void ensurePlayerSize();

	public abstract void pause();

}
