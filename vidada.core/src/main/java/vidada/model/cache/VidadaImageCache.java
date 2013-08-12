package vidada.model.cache;

import vidada.model.cache.async.AsyncLoadedImageCache;
import vidada.model.cache.crypto.CryptedImageFileCache;
import vidada.model.cache.crypto.ICacheKeyProvider;
import vidada.model.cache.crypto.VidadaCacheKeyProvider;

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
		boolean useAsyncLoader = true;
		boolean useMemoryCache = true;

		IImageCacheService imageCache;

		if(encryptCache){
			ICacheKeyProvider cacheKeyProvider = new VidadaCacheKeyProvider();
			imageCache = new CryptedImageFileCache(cacheKeyProvider);
		}else{
			imageCache = new ImageFileCache();
		}

		if(useAsyncLoader) imageCache = new AsyncLoadedImageCache(imageCache);
		if(useMemoryCache) imageCache = new MemoryImageCache(imageCache);

		setOriginalCache(imageCache);
	}


}
