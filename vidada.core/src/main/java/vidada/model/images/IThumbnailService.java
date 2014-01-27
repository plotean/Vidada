package vidada.model.images;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;
import archimedesJ.services.IService;

/**
 * IThumbnailService is responsible for creating, caching thumb images.
 * 
 * The image cache strategies can be complex since there may be multiple cache
 * layers, this service hides this complexity by providing a simple interface.
 * 
 * 
 * @author IsNull
 *
 */
public interface IThumbnailService extends IService{

	/**
	 * Retrieves a image container for a preview image of the given media item in the given size.
	 * 
	 * @param media The media item
	 * @param size The desired image size
	 * @return
	 */
	ImageContainer retrieveThumbnail(MediaItem media, Size size);

	/**
	 * Removes the current thumb of this media from all caches
	 * 
	 * Notifies the existing containers that they need to invalidate
	 * 
	 * @param media

	void removeImage(MediaItem media);
	 */

	/**
	 * Store the given image in the cache
	 * @param media
	 * @param image

	void storeImage(MediaItem media, IMemoryImage image);
	 */
}
