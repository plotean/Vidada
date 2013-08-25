package vidada.model.images.cache;

import java.util.HashSet;
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
public class ImageCacheProxyBase implements IImageCache{

	transient private IImageCache original;

	public ImageCacheProxyBase(IImageCache original){
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
		return original != null ? original.getImageById(id, size) : null;
	}

	@Override
	public Set<Size> getCachedDimensions(String id) {
		return original != null ? original.getCachedDimensions(id) : new HashSet<Size>();
	}

	@Override
	public boolean exists(String id, Size size) {
		return original != null ? original.exists(id, size) : false;
	}

	@Override
	public void storeImage(String id, IMemoryImage image) {
		if(original != null)
			original.storeImage(id, image);
	}

	@Override
	public void removeImage(String id) {
		if(original != null)
			original.removeImage(id);
	}

}
