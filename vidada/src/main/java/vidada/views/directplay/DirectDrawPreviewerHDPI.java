package vidada.views.directplay;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import vlcj.VideoSurfacePane;

/**
 * High DPI video renderer using direct draw (java2D)
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class DirectDrawPreviewerHDPI extends DirectDrawPreviewer {

	public DirectDrawPreviewerHDPI(){
		super();
	}


	@Override
	protected DirectMediaPlayer createMediaPlayer(int width, int height){
		return super.createMediaPlayer(width*2, height*2);
	}


	@Override
	protected VideoSurfacePane createImagePane(BufferedImage image){
		return new VideoSurfacePane(image, getVideoOverlayRenderer()) {

			private final Rectangle rectangle = new Rectangle();

			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;

				Rectangle r = g2.getClipBounds(rectangle);

				g2.drawImage(image, 0,0,
						(int)r.getWidth(),
						(int)r.getHeight(), null);

				if(postRenderer != null){
					postRenderer.render(g2);
				}
			}
		};
	}

}
