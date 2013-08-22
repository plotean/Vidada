package vidada.model.images;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vidada.model.ServiceProvider;
import vidada.model.images.ImageContainerBase.ImageChangedCallback;
import vidada.model.images.cache.IImageCacheService;
import vidada.model.images.cache.LeveledImageCache;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaItem;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;

public class ImageServiceBase implements IImageService {

	private final IImageCacheService localImageCache = ServiceProvider.Resolve(IImageCacheService.class);
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

	transient private final Map<MediaLibrary, IImageCacheService> combinedCachesMap = new HashMap<MediaLibrary, IImageCacheService>();


	private IImageCacheService getImageCache(MediaItem media){
		IImageCacheService imageCache;

		MediaSource source = media.getSource();
		if(source instanceof FileMediaSource){
			MediaLibrary library = ((FileMediaSource) source).getParentLibrary();

			imageCache = combinedCachesMap.get(library);

			if(imageCache == null){
				imageCache = new LeveledImageCache(
						localImageCache,
						library.getLibraryCache());

				combinedCachesMap.put(library, imageCache);
			}
		} else {
			imageCache = localImageCache;
		}

		return imageCache;
	}

	@Override
	public void removeImage(MediaItem media) {
		// clear the cache
		getImageCache(media).removeImage(media.getFilehash());

		Map<Size, ImageContainer> containerSize = imageContainerCache.get(media.getId());
		if(containerSize != null){
			for (ImageContainer container : containerSize.values()) {
				container.notifyImageChanged();
			}
		}
	}

	@Override
	public void storeImage(MediaItem media, IMemoryImage image){
		getImageCache(media).storeImage(media.getFilehash(), image);
	}


	private ImageContainer buildContainer(MediaItem media, Size size, ImageChangedCallback callback){
		return new ImageContainerBase(
				executorService,
				new ImageLoaderTask(getImageCache(media), media, size),
				callback);
	}



}
