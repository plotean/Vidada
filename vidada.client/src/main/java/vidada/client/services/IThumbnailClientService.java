package vidada.client.services;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

public interface IThumbnailClientService {

	/**
	 * Retrieves a preview image of the given media item in the requested size.
	 * 
	 * @param media The media item
	 * @param size The desired image size
	 * @return
	 */
	IMemoryImage retrieveThumbnail(MediaItem media, Size size);

	/**
	 * Renews the given media's thumbnail. 
	 * 
	 * First, this will clear any caches of the existing thumb.
	 * Then it will clear saved thumb position information saved in the media item.
	 * 
	 * The next request to retrieveThumbnail(...) will then cause the generation
	 * of a new thumbnail. 
	 * 
	 * @param media
	 * @param pos
	 * @return
	 */
	boolean renewThumbImage(MediaItem media, float pos);

}
