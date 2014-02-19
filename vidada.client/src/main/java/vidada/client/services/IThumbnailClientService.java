package vidada.client.services;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;

public interface IThumbnailClientService {

	//public IMemoryImage getThumbImage(MediaItem media, Size size);

	/**
	 * Retrieves a image container for a preview image of the given media item in the given size.
	 * 
	 * @param media The media item
	 * @param size The desired image size
	 * @return
	 */
	ImageContainer retrieveThumbnail(MediaItem media, Size size);

	/**
	 * 
	 * @param media
	 * @param size
	 * @param pos
	 * @return

	ImageContainer renewThumbImage(MovieMediaItem media, Size size, float pos);
	 */
}
