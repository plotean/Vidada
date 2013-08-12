package vidada.model.cache;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Set;

import archimedesJ.geometry.Size;

/**
 * Provides the ability to store and retrieve images
 * @author IsNull
 *
 */
public interface IImageCacheService {

	/**
	 * get the image from cache
	 * @param id The id of the image
	 * @param size The size of the image
	 * @return
	 */
	public abstract Image getImageById(String id, Size size);

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
	public abstract void storeImage(String id, BufferedImage image);


	/**
	 * Remove the given image from this cache
	 * @param id
	 */
	public abstract void removeImage(String id);


	//
	// Native image handling
	//

	/**
	 * Store the given image as native image for the given id
	 * @param id
	 * @param image
	 */
	void storeNativeImage(String id, BufferedImage image);

	/**
	 * Exists the native image of the given id?
	 * @param id
	 * @return
	 */
	boolean nativeImageExists(String id);

	/**
	 * Returns the native image of the given id
	 * @param id
	 * @return
	 */
	Image getNativeImage(String id);

	/**
	 * Returns the resolution of the native image
	 * @param id
	 * @return
	 */
	Size getNativeImageResolution(String id);
}
