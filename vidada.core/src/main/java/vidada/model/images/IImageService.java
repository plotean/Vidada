package vidada.model.images;

import vidada.model.images.ImageContainerBase.ImageChangedCallback;
import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;

public interface IImageService {

	/**
	 * Retrieves a image container for a preview image of the given media item in the given size.
	 * 
	 * @param media The media item
	 * @param size The desired image size
	 * @return
	 */
	ImageContainer retrieveImageContainer(MediaItem media, Size size, ImageChangedCallback callback);

	/**
	 * Removes the current thumb of this media from all caches
	 * 
	 * Notifies the existing containers that they need to invalidate
	 * 
	 * @param media
	 */
	void removeImage(MediaItem media);


	/**
	 * Store the given image in the cache
	 * @param media
	 * @param image
	 */
	void storeImage(MediaItem media, IMemoryImage image);
}
