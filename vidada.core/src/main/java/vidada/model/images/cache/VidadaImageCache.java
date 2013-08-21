package vidada.model.images.cache;

import java.net.URISyntaxException;

import vidada.model.images.cache.crypto.CryptedImageFileCache;
import vidada.model.images.cache.crypto.ICacheKeyProvider;
import vidada.model.images.cache.crypto.VidadaCacheKeyProvider;
import vidada.model.settings.GlobalSettings;
import archimedesJ.io.locations.DirectoiryLocation;

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
		DirectoiryLocation localImageCache;
		try {
			localImageCache = DirectoiryLocation.Factory.create(GlobalSettings.getInstance().getAbsoluteCachePath().toString());

			System.out.println("local image cache: " + localImageCache);

			if(encryptCache){
				ICacheKeyProvider cacheKeyProvider = new VidadaCacheKeyProvider();
				imageCache = new CryptedImageFileCache(localImageCache, cacheKeyProvider);
			}else{
				imageCache = new ImageFileCache(localImageCache);
			}

			if(useMemoryCache) imageCache = new MemoryImageCache(imageCache);

			setOriginalCache(imageCache);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


}
