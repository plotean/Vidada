package vidada.model.images;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vidada.model.ServiceProvider;
import vidada.model.images.ImageContainerBase.ImageChangedCallback;
import vidada.model.images.cache.IImageCacheService;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.media.MediaItem;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;

public class ImageServiceBase implements IImageService {

	private final IImageCacheService imageCache = ServiceProvider.Resolve(IImageCacheService.class);
	private final ExecutorService executorService = Executors.newFixedThreadPool(2);

	private final Map<Long, Map<Size, ImageContainer>> imageContainerCache;


	public ImageServiceBase(){
		this.imageContainerCache = Collections.synchronizedMap(
				new LRUCache<Long, Map<Size, ImageContainer>>((int)MemoryImageCache.MAX_MEMORY_SCALED_CACHE));
	}

	@Override
	public ImageContainer retrieveImageContainer(MediaItem media, Size size, ImageChangedCallback callback) {
		ImageContainer container = null;

		Map<Size, ImageContainer> containerSize = imageContainerCache.get(media.getId());

		if(containerSize != null){
			container = containerSize.get(size);
		}else {
			imageContainerCache.put(media.getId(), new HashMap<Size, ImageContainer>());
		}

		if(container == null){
			container = buildContainer(media, size, callback);
			imageContainerCache.get(media.getId())
			.put(size, container);
		}
		return container;
	}

	@Override
	public void removeImage(MediaItem media) {
		// clear the cache
		imageCache.removeImage(media.getFilehash());

		Map<Size, ImageContainer> containerSize = imageContainerCache.get(media.getId());
		if(containerSize != null){
			for (ImageContainer container : containerSize.values()) {
				container.notifyImageChanged();
			}
		}
	}

	@Override
	public void storeImage(MediaItem media, IMemoryImage image){
		imageCache.storeImage(media.getFilehash(), image);
	}


	private ImageContainer buildContainer(MediaItem media, Size size, ImageChangedCallback callback){
		return new ImageContainerBase(
				executorService,
				new ImageLoaderTask(imageCache, media, size),
				callback);
	}



}
