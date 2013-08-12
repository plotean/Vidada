package vidada.model.cache.async;

import java.awt.Image;

import vidada.model.cache.IImageCacheService;
import vidada.model.cache.ImageCacheProxyBase;
import archimedesJ.geometry.Size;

/**
 * AsyncLoadedImageCache
 * 
 * Injects AsycCacheImage to support asynchronous loading of
 * images from the underling cache. 
 * If the underlying cache is slow, this cache can greatly improve 
 * applications performance.
 * 
 * @author IsNull
 *
 */
public class AsyncLoadedImageCache extends ImageCacheProxyBase{

	/**
	 * Creates a new AsyncLoadedImageCache for injecting 
	 * AsycCacheImage to support lazy loading
	 * 
	 * @param original
	 */
	public AsyncLoadedImageCache(IImageCacheService originalCache) {
		super(originalCache);
	}

	@Override
	public Image getImageById(String id, Size size)
	{		
		if(nativeImageExists(id) || getOriginalCache().exists(id, size)){

			CachedImageInfo imageIdentifier = getCachedIdentifier(id, size);
			AsycCacheImage asyncImage = new AsycCacheImage(imageIdentifier);;

			return asyncImage;
		}else{
			System.err.println("AsyncLoadedImageCache.getImageById --> no cached image for " + id);
		}
		return null;
	}


	private CachedImageInfo getCachedIdentifier(String id, Size size){
		CachedImageInfo imageIdentifier = null;
		imageIdentifier = new CachedImageInfo(getOriginalCache(), id, size);
		return imageIdentifier;
	}



	@Override
	public boolean exists(String id, Size size)
	{
		boolean exist= getOriginalCache().exists(id, size) || nativeImageExists(id);
		//System.out.println("AsyncLoadedImageCache: " + exist);
		return exist;
	}

}
