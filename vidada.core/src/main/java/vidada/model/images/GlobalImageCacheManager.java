package vidada.model.images;

import archimedes.core.io.locations.DirectoryLocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.images.cache.IImageCache;
import vidada.model.images.cache.ImageCacheFactory;
import vidada.model.images.cache.MemoryImageCache;
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
 * Manages the global image cache and its slave caches.
 * @author IsNull
 *
 */
public class GlobalImageCacheManager {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(GlobalImageCacheManager.class.getName());

    transient private final ICredentialManager credentialManager =  ServiceProvider.Resolve(ICredentialManager.class);


	transient private final IImageCache globalCache;
	transient private final Map<MediaLibrary, IImageCache> combinedCachesMap = new HashMap<MediaLibrary, IImageCache>();

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new LocalImageCacheManager.
     * @param localCacheLocation Local central file based cache.
     */
	public GlobalImageCacheManager(File localCacheLocation){
        IImageCache globalCacheInstance;
        IImageCache localFileCache = openLocalFileCache(localCacheLocation);
        if(localFileCache != null){
            // We have a local file cache wrapped with a memory cache
            globalCacheInstance = new MemoryImageCache(localFileCache);
        }else{
            // We only have a memory cache as global cache
            globalCacheInstance = new MemoryImageCache();
        }
		globalCache = globalCacheInstance;
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
				imageCache = ImageCacheFactory.instance().leveledCache(globalCache, libraryCache);
				combinedCachesMap.put(library, imageCache);
			}
		} else {
			imageCache = globalCache;
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
	private synchronized IImageCache openLocalFileCache(File cacheLocation) {
		IImageCache cache = null;

        if(cacheLocation != null){
            File absCacheLocation = new File(cacheLocation.getAbsolutePath());
            DirectoryLocation localCacheLocation =
                    DirectoryLocation.Factory.create(absCacheLocation);

            cache = ImageCacheFactory.instance().openEncryptedCache(localCacheLocation, credentialManager);
        }
        return cache;
	}


}
