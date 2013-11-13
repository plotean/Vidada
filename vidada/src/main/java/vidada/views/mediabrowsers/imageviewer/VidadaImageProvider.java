package vidada.views.mediabrowsers.imageviewer;

import java.util.Map;

import vidada.model.ServiceProvider;
import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import vidada.model.media.images.ImageMediaItem;
import vidada.model.media.movies.MovieMediaItem;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.images.viewer.IImageProvider;
import archimedesJ.images.viewer.ISmartImage;
import archimedesJ.swing.components.thumbpresenter.JThumbInteractionController;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;

public class VidadaImageProvider implements IImageProvider {

	private final JThumbInteractionController interactionController;

	transient private final Map<MediaItem, ISmartImage> imageCache = new LRUCache<MediaItem, ISmartImage>(100);
	transient private final EventHandlerEx<EventArgs> CurrentImageChanged = new EventHandlerEx<EventArgs>();
	transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

	private ISmartImage currentImage;


	@Override
	public IEvent<EventArgs> getCurrentImageChanged() { return CurrentImageChanged; }


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
		currentImage = null;
		IBaseThumb selectedThumb = interactionController.getFirstSelected();
		if (selectedThumb instanceof IHaveMediaData) {

			MediaItem media = ((IHaveMediaData) selectedThumb).getMediaData();
			if (media instanceof ImageMediaItem) {
				if (!imageCache.containsKey(media)) {
					currentImage = new SmartImageMediaItemAdapter(imageFactory, (ImageMediaItem) media);
					imageCache.put(media, currentImage);
				}
			} else if (media instanceof MovieMediaItem) {
				if (!imageCache.containsKey(media)) {
					currentImage = new MoviePartWrapper((MovieMediaItem) media);
					imageCache.put(media, currentImage);
				}
			}
			setCurrentImage(imageCache.get(media));

		}
	}



	@Override
	public ISmartImage currentImage() {
		return currentImage;
	}

	protected void setCurrentImage(ISmartImage currentImage) {
		this.currentImage = currentImage;
		CurrentImageChanged.fireEvent(this, EventArgs.Empty);
	}

}
