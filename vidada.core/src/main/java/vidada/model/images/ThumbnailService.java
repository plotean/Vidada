package vidada.model.images;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vidada.model.ServiceProvider;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.media.MediaItem;
import vidada.model.media.store.IMediaStoreService;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;

public class ThumbnailService implements IThumbnailService {

	transient private final IMediaStoreService mediaStoreService = ServiceProvider.Resolve(IMediaStoreService.class);

	transient private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	/**
	 * Maps the media id to a size specific image container
	 */
	transient private final Map<Long, Map<Size, ImageContainer>> imageContainerCache;


	public ThumbnailService(){
		this.imageContainerCache = Collections.synchronizedMap(
				new LRUCache<Long, Map<Size, ImageContainer>>((int)MemoryImageCache.MAX_MEMORY_SCALED_CACHE));
	}

	@Override
	public ImageContainer retrieveThumbnail(MediaItem media, Size size) {
		ImageContainer container = null;

		Map<Size, ImageContainer> containerSize = imageContainerCache.get(media.getId());

		if(containerSize != null){
			container = containerSize.get(size);
		}else {
			imageContainerCache.put(media.getId(), new HashMap<Size, ImageContainer>());
		}

		if(container == null){
			container = buildContainer(media, size);
			imageContainerCache.get(media.getId()).put(size, container);
		}
		return container;
	}


	private ImageContainer buildContainer(MediaItem media, Size size){
		return new ImageContainerBase(
				executorService,
				new ImageLoaderTask(mediaStoreService.getStore(media), media, size));
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





}
