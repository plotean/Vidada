package vidada.model.images;

import vidada.model.images.ImageContainerBase.ImageChangedCallback;
import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaItem;
import vidada.model.security.ICredentialManager;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.ImageContainer;
import archimedesJ.io.locations.DirectoiryLocation;
import archimedesJ.services.IService;

/**
 * 
 * @author IsNull
 *
 */
public interface IImageService extends IService{

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


	/**
	 * Open the image cache at the given location.
	 * 
	 * @param cacheLocation The location of the cache
	 * @param credentialManager Credital manager to use if a password is required.
	 * @return
	 */
	IImageCache openEncryptedCache(DirectoiryLocation cacheLocation, ICredentialManager credentialManager);

	/**
	 * Open a non encrypted cache
	 * @param cacheLocation
	 * @return
	 */
	IImageCache openCache(DirectoiryLocation cacheLocation);
}
