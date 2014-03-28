package vidada.model.images;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import vidada.model.media.MediaItem;

public interface IThumbImageCreator {

	/**
	 * Is it possible to extract a thumb for the given media?
	 * @param media
	 * @return
	 */
	public abstract boolean canExtractThumb(MediaItem media);

	/**
	 * Extract a thumb from the given media in the specified size
	 *
	 * 
	 * @param media
	 * @param size
	 * @return
	 */
	public abstract IMemoryImage extractThumb(MediaItem media, Size size);

}