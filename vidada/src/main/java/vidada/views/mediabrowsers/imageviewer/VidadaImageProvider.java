package vidada.views.mediabrowsers.imageviewer;

import java.util.Map;

import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.movies.MovieMediaItem;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;
import archimedesJ.swing.components.imageviewer.IImageProvider;
import archimedesJ.swing.components.imageviewer.ISmartImage;
import archimedesJ.swing.components.thumbpresenter.JThumbInteractionController;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;

public class VidadaImageProvider implements IImageProvider {

	Map<MediaItem, ISmartImage> imageCache = new LRUCache<MediaItem, ISmartImage>(100);

	private final EventHandlerEx<EventArgs> CurrentImageChanged = new EventHandlerEx<EventArgs>();

	@Override
	public IEvent<EventArgs> getCurrentImageChanged() {
		return CurrentImageChanged;
	}

	private JThumbInteractionController interactionController;

	public VidadaImageProvider(JThumbInteractionController interactionController) {

		this.interactionController = interactionController;

		interactionController.getSelectionChangedEvent().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				updateCurrentImage();
			}
		});
		updateCurrentImage();
	}

	@Override
	public void navigateNext() {
		interactionController.navigateNext();
	}

	@Override
	public void navigatePrevious() {
		interactionController.navigatePrevious();
	}

	private void updateCurrentImage() {
		smartImage = null;
		IBaseThumb selectedThumb = interactionController.getFirstSelected();
		if (selectedThumb instanceof IHaveMediaData) {

			MediaItem media = ((IHaveMediaData) selectedThumb).getMediaData();
			if (media instanceof ImageMediaItem) {
				if (!imageCache.containsKey(media)) {
					smartImage = new ImagePartWrapper((ImageMediaItem) media);
					imageCache.put(media, smartImage);
				}
			} else if (media instanceof MovieMediaItem) {
				if (!imageCache.containsKey(media)) {
					smartImage = new MoviePartWrapper((MovieMediaItem) media);
					imageCache.put(media, smartImage);
				}
			}
			setCurrentImage(imageCache.get(media));

		}
	}

	ISmartImage smartImage;

	@Override
	public ISmartImage currentImage() {
		return smartImage;
	}

	protected void setCurrentImage(ISmartImage currentImage) {
		smartImage = currentImage;
		CurrentImageChanged.fireEvent(this, EventArgs.Empty);
	}

}
