package vidada.model.images.cache.crypto;

import archimedes.core.io.locations.DirectoryLocation;
import vidada.model.images.cache.IImageCache;
import vidada.model.images.cache.ImageFileCache;
import vidada.model.images.cache.LeveledImageCache;
import vidada.model.security.AuthenticationException;
import vidada.model.security.ICredentialManager;

public class ImageCacheFactory {

	/**
	 * Build a leveled cache
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
			System.err.println("ImageCacheFactory: firstLevelCache is NULL!");
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
			System.err.println("ImageCacheFactory: AuthenticationException - openCache failed!");
			e.printStackTrace();
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
