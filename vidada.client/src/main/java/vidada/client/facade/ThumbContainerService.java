package vidada.client.facade;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vidada.client.services.IThumbnailClientService;
import vidada.model.images.ImageContainerBase;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.media.MediaItem;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;

/**
 * Wraps a {@link IThumbnailClientService} in an simpler to use facade.
 * Primary purpose is to shield clients form the complexity of
 * possible long running thumbnail generation and fetching.
 * 
 * Using this service, clients recive an {@link ImageContainer} which encapsulates
 * the async loading task of an thumbnail. The tasks are internally queued and executed in order. 
 * Further, the service ensures that only a small amount of parallel thumbnails are being loaded. 
 * (Fetching a thumbnail can be very CPU consuming)
 * 
 * @author IsNull
 *
 */
public class ThumbContainerService implements IThumbConatinerService {

	/**
	 * Defines how many thumbnails can be fetched at the same time.
	 */
	transient private static int MAX_PARALLEL_THUMBFETCHER = 1;

	transient private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_PARALLEL_THUMBFETCHER);
	/**
	 * Maps the media id to a size specific image container
	 */
	transient private final Map<String, Map<Size, ImageContainer>> imageContainerCache;

	transient private final IThumbnailClientService thumbnailClientService;

	/**
	 * Creates a new ThumbContainerService
	 * @param thumbnailClientService
	 */
	public ThumbContainerService(IThumbnailClientService thumbnailClientService){
		if(thumbnailClientService == null) throw new IllegalArgumentException("thumbnailClientService must not be Null!");

		this.thumbnailClientService = thumbnailClientService;
		this.imageContainerCache = Collections.synchronizedMap(
				new LRUCache<String, Map<Size, ImageContainer>>((int)MemoryImageCache.MAX_MEMORY_SCALED_CACHE));
	}


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/* (non-Javadoc)
	 * @see vidada.client.facade.IThumConatinerService#retrieveThumbnail(vidada.model.media.MediaItem, archimedesJ.geometry.Size)
	 */
	@Override
	public ImageContainer retrieveThumbnail(MediaItem media, Size size) {
		ImageContainer container = null;

		Map<Size, ImageContainer> containerSize = imageContainerCache.get(media.getFilehash());

		if(containerSize != null){
			container = containerSize.get(size);
		}else {
			imageContainerCache.put(media.getFilehash(), new HashMap<Size, ImageContainer>());
		}

		if(container == null){
			container = buildContainer(media, size);
			imageContainerCache.get(media.getFilehash()).put(size, container);
		}
		return container;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	private ImageContainer buildContainer(MediaItem media, Size size){
		return new ImageContainerBase(
				executorService,
				new ImageLoaderTask(media, size));
	}

	private IMemoryImage getThumbImage(MediaItem media, Size size) {
		// TODO local file cache?
		return thumbnailClientService.retrieveThumbnail(media, size);
	}

	/***************************************************************************
	 *                                                                         *
	 * Inner Classes                                                           *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Represents a task which can retrieve a thumb for the given media item
	 * @author IsNull
	 *
	 */
	class ImageLoaderTask implements Callable<IMemoryImage>
	{
		transient private final MediaItem media;
		transient private final Size size;

		public ImageLoaderTask(MediaItem media, Size size){
			this.media = media;
			this.size = size;
		}

		@Override
		public IMemoryImage call() throws Exception {
			return getThumbImage(media, size);
		}
	}
}
