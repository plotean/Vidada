package vidada.model.images;

import archimedes.core.io.locations.DirectoryLocation;
import vidada.model.images.cache.IImageCache;
import vidada.model.images.cache.ImageCacheProxyBase;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.images.cache.ImageCacheFactory;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaLibrary;
import vidada.model.media.source.MediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.security.ICredentialManager;
import vidada.services.ServiceProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the local image caches
 * @author IsNull
 *
 */
public class LocalImageCacheManager {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

	transient private final ICredentialManager credentialManager =  ServiceProvider.Resolve(ICredentialManager.class);

	transient private final IImageCache localImageCache;
	transient private final ImageCacheFactory cacheFactory = new ImageCacheFactory();
	transient private final Map<MediaLibrary, IImageCache> combinedCachesMap = new HashMap<MediaLibrary, IImageCache>();



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new LocalImageCacheManager.
     * @param cacheLocation Local central file based cache.
     */
	public LocalImageCacheManager(File cacheLocation){
		localImageCache = openLocalCache(cacheLocation);
	}


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


	/**
	 * Gets the image cache for the given media
	 * @param media
	 * @return
	 */
	public IImageCache getImageCache(MediaItem media){
		IImageCache imageCache;

		MediaSource source = media.getSource();
		if(source != null)
		{
			MediaLibrary library = ((MediaSourceLocal)source).getParentLibrary();

			imageCache = combinedCachesMap.get(library);
			if(imageCache == null){
				IImageCache libraryCache = library.getLibraryCache();
				imageCache = cacheFactory.leveledCache(localImageCache, libraryCache);
				combinedCachesMap.put(library, imageCache);
			}
		} else {
			imageCache = localImageCache;
		}

		return imageCache;
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


	/**
	 * Opens the local image cache
	 * Should this fail, it will return a dummy cache
	 * @return
	 */
	private synchronized IImageCache openLocalCache(File cacheLocation) {
		IImageCache cache = null;

		File absCacheLocation = new File(cacheLocation.getAbsolutePath());
		DirectoryLocation localCacheLocation =
				DirectoryLocation.Factory.create(absCacheLocation);

		cache = cacheFactory.openEncryptedCache(localCacheLocation, credentialManager);

		if(cache != null){
			return cache;
		}else{
			System.err.println("LocalImageCacheManager: Injecting a MemoryCache to replace LocalFile Cache");
			return new MemoryImageCache(new ImageCacheProxyBase(null));
		}

	}


}
