package vidada.client.services;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

public interface IThumbnailClientService {

	//public IMemoryImage getThumbImage(MediaItem media, Size size);

	/**
	 * Retrieves a preview image of the given media item in the requested size.
	 * 
	 * @param media The media item
	 * @param size The desired image size
	 * @return
	 */
	IMemoryImage retrieveThumbnail(MediaItem media, Size size);

	/**
	 * 
	 * @param media
	 * @param size
	 * @param pos
	 * @return

	ImageContainer renewThumbImage(MovieMediaItem media, Size size, float pos);
	 */
}
