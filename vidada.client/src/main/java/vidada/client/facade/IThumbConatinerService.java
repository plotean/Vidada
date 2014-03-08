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

}