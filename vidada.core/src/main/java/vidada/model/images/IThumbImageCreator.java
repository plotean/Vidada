package vidada.model.images;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

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


	/**
	 * Updates the resolution property by reading out the medias resolution
	 * @param media
	 * @return
	 */
	public abstract boolean updateResolution(MediaItem media);
}