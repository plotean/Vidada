package vidada.model.images.cache;

import java.util.Set;

import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * Base class for image cache proxy. 
 * This implementation redirects all calls to this instance down to the "original".
 * 
 * Subclasses may override methods of the proxy to transparently inject functionality.
 * @author IsNull
 *
 */
public abstract class ImageCacheProxyBase implements IImageCache{

	private IImageCache original;

	protected ImageCacheProxyBase(IImageCache original){
		this.original = original;
	}

	protected final void setOriginalCache(IImageCache original) {
		this.original = original;
	} 

	protected final IImageCache getOriginalCache() {
		return original;
	} 

	@Override
	public IMemoryImage getImageById(String id, Size size) {
		return original.getImageById(id, size);
	}

	@Override
	public Set<Size> getCachedDimensions(String id) {
		return original.getCachedDimensions(id);
	}

	@Override
	public boolean exists(String id, Size size) {
		return original.exists(id, size);
	}

	@Override
	public void storeImage(String id, IMemoryImage image) {
		original.storeImage(id, image);

	}

	@Override
	public void removeImage(String id) {
		original.removeImage(id);
	}

}
