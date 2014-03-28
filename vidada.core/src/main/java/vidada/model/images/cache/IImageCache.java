package vidada.model.images.cache;


import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;

import java.util.Set;

/**
 * Represents an image cache.
 * 
 * Each image is identified by its unique id AND the size.
 * 
 * @author IsNull
 *
 */
public interface IImageCache {

	/**
	 * get the image from cache
	 * @param id The id of the image
	 * @param size The size of the image
	 * @return
	 */
	public abstract IMemoryImage getImageById(String id, Size size);

	/**
	 * For the given id, returns all available image dimensions
	 * @param id The id of the image
	 * @return
	 */
	public abstract Set<Size> getCachedDimensions(String id);

	/**
	 * Does the given image exist in this cache?
	 * @param id
	 * @param size
	 * @return
	 */
	public abstract boolean exists(String id, Size size);

	/**
	 * Persist the given image in this cache
	 * @param id
	 * @param image
	 */
	public abstract void storeImage(String id, IMemoryImage image);


	/**
	 * Remove the given image from this cache
	 * @param id
	 */
	public abstract void removeImage(String id);
}
