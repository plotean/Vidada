package vidada.views.mediabrowsers.mediaBrowser.thumbviewer.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import archimedesJ.swing.components.accessability.AccessabilityRenderer;
import archimedesJ.swing.components.starrating.BufferedStarRaterRenderer;
import archimedesJ.swing.components.starrating.StarRaterRenderer;
import archimedesJ.swing.components.thumbpresenter.items.IMediaDataThumb;
import archimedesJ.swing.components.thumbpresenter.renderer.HDPIThumbRenderer;

/**
 * Customized ThumbRenderer for Vidada
 * 
 * Supporting rendering of 
 * -The Rating Stars
 * -Error Icons
 * 
 * @author IsNull
 * 
 */
public class VidadaThumbRenderer extends HDPIThumbRenderer {

	private final StarRaterRenderer starRatingRenderer;
	private final AccessabilityRenderer accessabilityRenderer;

	public VidadaThumbRenderer(boolean useHDPIrenderer) {
		super(useHDPIrenderer);

		starRatingRenderer = new BufferedStarRaterRenderer();
		starRatingRenderer.setDrawAvaiableStars(false);
		starRatingRenderer.setFillColor(Color.yellow);
		starRatingRenderer.setStrokeColor(new Color(0x69, 0x69, 0x69));

		accessabilityRenderer = new AccessabilityRenderer();
		accessabilityRenderer.setBackgroundColor(Color.LIGHT_GRAY);
	}

	@Override
	public void drawPostContent(ImageObserver imageObserver, Graphics2D g,
			Rectangle r, int index, IMediaDataThumb item) {

		super.drawPostContent(imageObserver, g, r, index, item);

		// draw rating
		if (item instanceof IHaveMediaData) {

			MediaItem mediaData = ((IHaveMediaData)item).getMediaData();

			if(mediaData != null){
				// apply responsive design

				if (r.width > 150) {
					// starRatingRenderer.setDrawBackground(false);
					starRatingRenderer.draw(g, mediaData.getRating(), r.x + 4, r.y + 2);
				}

				if (!mediaData.isAvailable()) {
					accessabilityRenderer.draw(imageObserver, g, r, item); }
			}
		}
	}
}
