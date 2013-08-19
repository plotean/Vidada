package vidada.model.images.cache;

import vidada.model.images.cache.crypto.CryptedImageFileCache;
import vidada.model.images.cache.crypto.ICacheKeyProvider;
import vidada.model.images.cache.crypto.VidadaCacheKeyProvider;

/**
 * Composition of the vidada image cache
 * 
 * @author IsNull
 *
 */
public class VidadaImageCache  extends ImageCacheProxyBase{

	public VidadaImageCache() {
		super(null);

		boolean encryptCache = true;
		boolean useMemoryCache = false; // ImageContainers are already cached in memory

		IImageCacheService imageCache;

		if(encryptCache){
			ICacheKeyProvider cacheKeyProvider = new VidadaCacheKeyProvider();
			imageCache = new CryptedImageFileCache(cacheKeyProvider);
		}else{
			imageCache = new ImageFileCache();
		}

		if(useMemoryCache) imageCache = new MemoryImageCache(imageCache);

		setOriginalCache(imageCache);
	}


}
