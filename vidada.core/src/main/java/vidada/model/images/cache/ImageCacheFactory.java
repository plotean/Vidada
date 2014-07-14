package vidada.model.images.cache;

import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.security.AuthenticationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.images.cache.crypto.CryptedImageFileCache;
import vidada.model.images.cache.crypto.ICacheKeyProvider;
import vidada.model.images.cache.crypto.VidadaCacheKeyProvider;
import vidada.model.security.ICredentialManager;

/**
 * Provides helper methods to build image caches.
 */
public class ImageCacheFactory {

    private static final Logger logger = LogManager.getLogger(ImageCacheFactory.class.getName());
    private static final ImageCacheFactory instance = new ImageCacheFactory();

    private ImageCacheFactory(){

    }

    /**
     * Gets the default cache factory
     * @return
     */
    public synchronized static ImageCacheFactory instance(){
        return instance;
    }

    /**
	 * Build a leveled cache form the given two caches.
     *
	 * @param firstLevelCache
	 * @param secondLevelCache
	 * @return
	 */
	public IImageCache leveledCache(IImageCache firstLevelCache, IImageCache secondLevelCache){
		IImageCache imageCache = null;
		if(firstLevelCache != null){
			if(secondLevelCache != null){
				imageCache = new LeveledImageCache(
						firstLevelCache,
						secondLevelCache
						);
			}else {
				imageCache = firstLevelCache;
			}
		}else if(secondLevelCache != null){
			imageCache = secondLevelCache;
            logger.warn("Can not create leveled cache, since firstLevelCache is NULL!");
		}else{
			// both caches are null
			return null;
		}
		return imageCache;
	}


	/**
	 * Creates a location based encrypted image cache. The concrete used cache implementation
	 * depends on the location type.
	 */
	public IImageCache openEncryptedCache(DirectoryLocation cacheLocation, ICredentialManager credentialManager) {
		ICacheKeyProvider cacheKeyProvider = new VidadaCacheKeyProvider(credentialManager);
		IImageCache cache = null;
		try {
			cache = new CryptedImageFileCache(cacheLocation, cacheKeyProvider);
		} catch (AuthenticationException e) {
            logger.error("AuthenticationException - openCache failed!", e);
		}
		return cache;
	}

	/**
	 * Opens an non encrypted cache. 
	 */
	public IImageCache openCache(DirectoryLocation cacheLocation) {
		return new ImageFileCache(cacheLocation);
	}
}
