package vidada.model.images;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vidada.model.ServiceProvider;
import vidada.model.images.ImageContainerBase.ImageChangedCallback;
import vidada.model.images.cache.IImageCache;
import vidada.model.images.cache.ImageCacheProxyBase;
import vidada.model.images.cache.ImageFileCache;
import vidada.model.images.cache.LeveledImageCache;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.images.cache.crypto.CryptedImageFileCache;
import vidada.model.images.cache.crypto.ICacheKeyProvider;
import vidada.model.images.cache.crypto.VidadaCacheKeyProvider;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaItem;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.model.security.AuthenticationException;
import vidada.model.security.ICredentialManager;
import vidada.model.settings.GlobalSettings;
import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;
import archimedesJ.io.locations.DirectoiryLocation;

public class ImageServiceBase implements IImageService {

	transient private final ICredentialManager credentialManager =  ServiceProvider.Resolve(ICredentialManager.class);
	transient private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	transient private final Map<Long, Map<Size, ImageContainer>> imageContainerCache;
	transient private final IImageCache localImageCache;

	public ImageServiceBase(){
		this.imageContainerCache = Collections.synchronizedMap(
				new LRUCache<Long, Map<Size, ImageContainer>>((int)MemoryImageCache.MAX_MEMORY_SCALED_CACHE));

		localImageCache = openLocalCache();
	}

	/**
	 * Opens the local image cache
	 * Should this fail, it will return a dummy cache
	 * @return
	 */
	private IImageCache openLocalCache() {
		DirectoiryLocation localCacheLocation = null;
		try {
			localCacheLocation = DirectoiryLocation.Factory.create(
					GlobalSettings.getInstance().getAbsoluteCachePath().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		IImageCache cache = openEncryptedCache(localCacheLocation, credentialManager);
		if(cache != null){
			return cache;
		}else{
			System.out.println("Injecting a MemoryCache to replace LocalFile Cache");
			return new MemoryImageCache(new ImageCacheProxyBase(null));
		}
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

	transient private final Map<MediaLibrary, IImageCache> combinedCachesMap = new HashMap<MediaLibrary, IImageCache>();


	private IImageCache getImageCache(MediaItem media){
		IImageCache imageCache;

		MediaSource source = media.getSource();
		if(source instanceof FileMediaSource){
			MediaLibrary library = ((FileMediaSource) source).getParentLibrary();

			imageCache = combinedCachesMap.get(library);

			if(imageCache == null){

				IImageCache libraryCache = library.getLibraryCache();

				if(localImageCache != null){
					if(libraryCache != null){
						imageCache = new LeveledImageCache(
								localImageCache,
								libraryCache
								);
					}else {
						imageCache = localImageCache;
					}
				}else {
					if(libraryCache != null){
						imageCache = libraryCache;
					}
					System.err.println("getImageCache -> localImageCache is NULL!");
				}

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

	/**
	 * Creates a location based encrypted image cache. The concrete used cache implementation
	 * depends on the location type.
	 */
	@Override
	public IImageCache openEncryptedCache(DirectoiryLocation cacheLocation, ICredentialManager credentialManager) {
		ICacheKeyProvider cacheKeyProvider = new VidadaCacheKeyProvider(credentialManager);
		IImageCache cache = null;
		try {
			cache = new CryptedImageFileCache(cacheLocation, cacheKeyProvider);
		} catch (AuthenticationException e) {
			System.err.println("openCache failed!");
			e.printStackTrace();
		}
		return cache;
	}

	/**
	 * Opens an non encrypted cache. 
	 */
	@Override
	public IImageCache openCache(DirectoiryLocation cacheLocation) {
		return new ImageFileCache(cacheLocation);
	}

}
