package vidada.views.directplay;

import javax.swing.JPanel;

import vidada.viewsFX.player.IMediaController;
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
		getMediaController().refresh();
	}


	/**
	 * Get the media controller
	 * @return
	 */
	public abstract IMediaController getMediaController();

}
