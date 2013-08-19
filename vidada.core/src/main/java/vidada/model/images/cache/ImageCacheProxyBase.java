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
public abstract class ImageCacheProxyBase implements IImageCacheService{

	private IImageCacheService original;

	protected ImageCacheProxyBase(IImageCacheService original){
		this.original = original;
	}

	protected final void setOriginalCache(IImageCacheService original) {
		this.original = original;
	} 

	protected final IImageCacheService getOriginalCache() {
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

	@Override
	public void storeNativeImage(String id, IMemoryImage image) {
		original.storeNativeImage(id, image);
	}

	@Override
	public boolean nativeImageExists(String id) {
		return original.nativeImageExists(id);
	}

	@Override
	public IMemoryImage getNativeImage(String id) {
		return original.getNativeImage(id);
	}

	@Override
	public Size getNativeImageResolution(String id) {
		return original.getNativeImageResolution(id);
	}

}
