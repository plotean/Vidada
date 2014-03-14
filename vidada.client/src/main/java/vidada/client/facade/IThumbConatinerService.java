package vidada.client.facade;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;

public interface IThumbConatinerService {

	/**
	 * Retrieves a {@link ImageContainer} which links to the desired thumbnail.
	 * This call returns immediately, independent if the actual task is queued or already executed.
	 * @param media
	 * @param size
	 * @return
	 */
	public abstract ImageContainer retrieveThumbnail(MediaItem media, Size size);


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