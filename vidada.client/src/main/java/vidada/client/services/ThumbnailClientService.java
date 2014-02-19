package vidada.client.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vidada.IVidadaServer;
import vidada.client.VidadaClientManager;
import vidada.model.images.ImageContainerBase;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.media.MediaItem;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;

public class ThumbnailClientService extends ClientService implements IThumbnailClientService {

	transient private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	/**
	 * Maps the media id to a size specific image container
	 */
	transient private final Map<String, Map<Size, ImageContainer>> imageContainerCache;


	public ThumbnailClientService(VidadaClientManager manager) {
		super(manager);
		this.imageContainerCache = Collections.synchronizedMap(
				new LRUCache<String, Map<Size, ImageContainer>>((int)MemoryImageCache.MAX_MEMORY_SCALED_CACHE));
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

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

	private IMemoryImage getThumbImage(MediaItem media, Size size) {
		// TODO maybe add local cache?
		IVidadaServer origin = getClientManager().getOriginServer(media);
		return origin.getThumbnailService().getThumbImage(media, size);
	}


	private ImageContainer buildContainer(MediaItem media, Size size){
		return new ImageContainerBase(
				executorService,
				new ImageLoaderTask(media, size));
	}

	/*
	private void removeImage(MediaItem media) {
		// clear the cache
		getImageCache(media).removeImage(media.getFilehash());

		Map<Size, ImageContainer> containerSize = imageContainerCache.get(media.getId());
		if(containerSize != null){
			for (ImageContainer container : containerSize.values()) {
				container.notifyImageChanged();
			}
		}
	}


	private void storeImage(MediaItem media, IMemoryImage image){
		getImageCache(media).storeImage(media.getFilehash(), image);
	}
	 */



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
