package vidada.model.cache.async;

import vidada.model.cache.IImageCacheService;
import archimedesJ.geometry.Size;
import archimedesJ.swing.images.IImageIdentifier;

/**
 * Holds information to load a image
 * @author IsNull
 *
 */
public class CachedImageInfo implements IImageIdentifier {

	public final IImageCacheService imageCache;
	public final Size dimension;
	public final String id;	

	/**
	 * 
	 * @param imageCache
	 * @param id
	 * @param dimension
	 */
	public CachedImageInfo(IImageCacheService imageCache, String id, Size dimension){
		this.imageCache = imageCache;
		this.id = id;
		this.dimension = dimension;
	}

	@Override
	public int getId() {
		return id.hashCode();
	}
}
